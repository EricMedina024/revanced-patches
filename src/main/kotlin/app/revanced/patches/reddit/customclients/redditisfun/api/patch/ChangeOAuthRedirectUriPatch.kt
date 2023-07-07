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
        forEach { methodFingerprint ->
            methodFingerprint.scanResult.stringsScanResult!!.matches.forEach { clientIdInstruction ->
                methodFingerprint.mutableMethod.apply {
                    val clientIdIndex = clientIdInstruction.index
                    val clientIdRegister = getInstruction<OneRegisterInstruction>(clientIdIndex).registerA

                    methodFingerprint.mutableMethod.replaceInstruction(
                        clientIdIndex,
                        "const-string v$clientIdRegister, \"$redirectUri\""
                    )
                }
            }
        }

        return PatchResultSuccess()
    }

    companion object Options : AbstractChangeOAuthRedirectUriPatch.Options.ChangeOAuthRedirectUriOptionsContainer()
}
