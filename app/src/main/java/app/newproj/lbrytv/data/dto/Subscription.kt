package app.newproj.lbrytv.data.dto

import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName

data class Subscription(
    @SerializedName("channel_name")
    @ColumnInfo(name = "channel_name")
    val channelName: String? = null,

    @SerializedName("claim_id")
    @ColumnInfo(name = "claim_id")
    val claimId: String? = null,

    @SerializedName("created_at")
    @Ignore
    val createdAt: String? = null,

    @SerializedName("id")
    @ColumnInfo(name = "id")
    val id: Int? = null,

    @SerializedName("install_id")
    @Ignore
    val installId: Int? = null,

    @SerializedName("is_notifications_disabled")
    @Ignore
    val isNotificationsDisabled: Boolean? = null,

    @SerializedName("updated_at")
    @Ignore
    val updatedAt: String? = null,

    @SerializedName("user_id")
    @Ignore
    val userId: Int? = null
)
