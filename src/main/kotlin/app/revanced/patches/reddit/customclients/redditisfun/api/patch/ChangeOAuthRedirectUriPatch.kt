package app.revanced.patches.reddit.customclients.redditisfun.api.patch

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult.MethodFingerprintScanResult.StringsScanResult.StringMatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.reddit.customclients.AbstractChangeOAuthRedirectUriPatch
import app.revanced.patches.reddit.customclients.ChangeOAuthRedirectUriPatchAnnotation
import app.revanced.patches.reddit.customclients.redditisfun.api.fingerprints.RedirectUriFingerprint
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@ChangeOAuthRedirectUriPatchAnnotation
@Description("Changes the OAuth redirect URI.")
@Compatibility([Package("com.andrewshu.android.reddit"), Package("com.andrewshu.android.redditdonation")])
class ChangeOAuthRedirectUriPatch : AbstractChangeOAuthRedirectUriPatch(
    Options,
    listOf(RedirectUriFingerprint)
) {
    override fun List<MethodFingerprintResult>.patch(context: BytecodeContext): PatchResult {
        /**
         * Replaces a one register instruction with a const-string instruction
         * at the index returned by [getReplacementIndex].
         *
         * @param string The string to replace the instruction with.
         * @param getReplacementIndex A function that returns the index of the instruction to replace
         * using the [StringMatch] list from the [MethodFingerprintResult].
         */
        fun MethodFingerprintResult.replaceWith(
            string: String,
            getReplacementIndex: List<StringMatch>.() -> Int,
        ) = mutableMethod.apply {
            val replacementIndex = scanResult.stringsScanResult!!.matches.getReplacementIndex()
            val clientIdRegister = getInstruction<OneRegisterInstruction>(replacementIndex).registerA

            replaceInstruction(replacementIndex, "const-string v$clientIdRegister, \"$string\"")
        }

        // Patch OAuth authorization.
        forEach { it.replaceWith(redirectUri!!) { first().index + 4 } }

        return PatchResultSuccess()
    }

    companion object Options : AbstractChangeOAuthRedirectUriPatch.Options.ChangeOAuthRedirectUriOptionsContainer()
}
