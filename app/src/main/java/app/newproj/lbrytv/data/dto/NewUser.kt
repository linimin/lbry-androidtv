package app.newproj.lbrytv.data.dto

import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName

data class NewUser(
    @SerializedName("auth_token")
    @Ignore
    val plainTextAuthToken: String? = null,

    @SerializedName("country")
    @Ignore
    val country: Any? = null,

    @SerializedName("created_at")
    @Ignore
    val createdAt: String? = null,

    @SerializedName("family_name")
    @Ignore
    val familyName: Any? = null,

    @SerializedName("given_name")
    @Ignore
    val givenName: Any? = null,

    @SerializedName("id")
    @ColumnInfo(name = "id")
    val id: Int,

    @SerializedName("invite_reward_claimed")
    @Ignore
    val inviteRewardClaimed: Boolean? = null,

    @SerializedName("invited_at")
    @Ignore
    val invitedAt: Any? = null,

    @SerializedName("invited_by_id")
    @Ignore
    val invitedById: Any? = null,

    @SerializedName("invites_remaining")
    @Ignore
    val invitesRemaining: Int? = null,

    @SerializedName("is_email_enabled")
    @Ignore
    val isEmailEnabled: Boolean? = null,

    @SerializedName("is_odysee_user")
    @Ignore
    val isOdyseeUser: Boolean? = null,

    @SerializedName("is_reward_approved")
    @Ignore
    val isRewardApproved: Boolean? = null,

    @SerializedName("language")
    @Ignore
    val language: String? = null,

    @SerializedName("location")
    @Ignore
    val location: Any? = null,

    @SerializedName("manual_approval_user_id")
    @Ignore
    val manualApprovalUserId: Any? = null,

    @SerializedName("prev_settings")
    @Ignore
    val prevSettings: Any? = null,

    @SerializedName("publish_id")
    @Ignore
    val publishId: Any? = null,

    @SerializedName("reward_status_change_trigger")
    @Ignore
    val rewardStatusChangeTrigger: Any? = null,

    @SerializedName("settings")
    @Ignore
    val settings: Any? = null,

    @SerializedName("updated_at")
    @Ignore
    val updatedAt: String? = null
)
