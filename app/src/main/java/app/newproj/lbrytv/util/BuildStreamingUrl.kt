package app.newproj.lbrytv.util

import app.newproj.lbrytv.data.entity.Claim

fun Claim.streamingUrl(): String {
    return "https://cdn.lbryplayer.xyz/content/claims/$name/$id/stream"
}
