package app.newproj.lbrytv.data.dto

import com.google.gson.annotations.SerializedName

enum class ClaimType {
    @SerializedName("stream")
    STREAM_CLAIM,

    @SerializedName("channel")
    CHANNEL_CLAIM,
}
