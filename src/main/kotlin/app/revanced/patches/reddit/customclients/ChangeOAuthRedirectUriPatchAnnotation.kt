package app.revanced.patches.reddit.customclients

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.patch.annotations.Patch

@Target(AnnotationTarget.CLASS)
@Patch
@Name("change-oauth-redirect-uri")
annotation class ChangeOAuthRedirectUriPatchAnnotation