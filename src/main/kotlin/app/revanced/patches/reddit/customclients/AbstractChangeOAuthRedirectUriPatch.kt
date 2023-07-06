package app.revanced.patches.reddit.customclients

import android.os.Environment
import app.revanced.extensions.toErrorResult
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.patch.*
import java.io.File

abstract class AbstractChangeOAuthRedirectUriPatch(
    private val options: ChangeOAuthRedirectUriOptionsContainer,
    private val fingerprints: List<MethodFingerprint>
) : BytecodePatch(fingerprints) {
    override fun execute(context: BytecodeContext): PatchResult {
        if (options.redirectUri == null) {
            // Ensure device runs Android.
            try {
                Class.forName("android.os.Environment")
            } catch (e: ClassNotFoundException) {
                return PatchResultError("No redirect URI provided")
            }

            File(Environment.getExternalStorageDirectory(), "reddit_redirect_uri_revanced.txt").also {
                if (it.exists()) return@also

                val error = """
                    In order to use this patch, you need to provide a redirect URI.
                    You can do this by creating a file at ${it.absolutePath} with the redirect URI as its content.
                    Alternatively, you can provide the redirect URI using patch options.
                """.trimIndent()

                return PatchResultError(error)
            }.let { options.redirectUri = it.readText().trim() }
        }

        return fingerprints.map { it.result ?: throw it.toErrorResult() }.patch(context)
    }

    abstract fun List<MethodFingerprintResult>.patch(context: BytecodeContext): PatchResult

    companion object Options {
        open class ChangeOAuthRedirectUriOptionsContainer : OptionsContainer() {
            var redirectUri by option(
                PatchOption.StringOption(
                    "redirect-uri",
                    null,
                    "Redirect URI",
                    "The Reddit OAuth redirect URI"
                )
            )
        }
    }
}