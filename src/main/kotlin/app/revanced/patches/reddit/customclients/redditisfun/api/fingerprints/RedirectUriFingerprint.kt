package app.revanced.patches.reddit.customclients.redditisfun.api.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object RedirectUriFingerprint : MethodFingerprint(strings = listOf("redditisfun://auth"))