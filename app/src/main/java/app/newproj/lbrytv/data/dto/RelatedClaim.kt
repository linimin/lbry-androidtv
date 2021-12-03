package app.newproj.lbrytv.data.dto

import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName

data class RelatedClaim(
    @SerializedName("channel")
    @Ignore
    val channel: String? = null,

    @SerializedName("channel_claim_id")
    @Ignore
    val channelClaimId: String? = null,

    @SerializedName("claimId")
    @ColumnInfo(name = "id")
    val id: String,

    @SerializedName("duration")
    @Ignore
    val duration: Int? = null,

    @SerializedName("fee")
    @Ignore
    val fee: Int? = null,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String? = null,

    @SerializedName("release_time")
    @ColumnInfo(name = "release_time")
    val releaseTime: String? = null,

    @SerializedName("thumbnail_url")
    @ColumnInfo(name = "thumbnail")
    val thumbnailUrl: String? = null,

    @SerializedName("title")
    @ColumnInfo(name = "title")
    val title: String? = null
)
