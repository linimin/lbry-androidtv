package app.newproj.lbrytv.data.dto

import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName

data class TokenUser(
    @SerializedName("country")
    @Ignore
    val country: Any? = null,

    @SerializedName("created_at")
    @Ignore
    val createdAt: String? = null,

    @SerializedName("device_types")
    @Ignore
    val deviceTypes: List<Any?>? = null,

    @SerializedName("experimental_ui")
    @Ignore
    val experimentalUi: Boolean? = null,

    @SerializedName("family_name")
    @Ignore
    val familyName: Any? = null,

    @SerializedName("given_name")
    @Ignore
    val givenName: Any? = null,

    @SerializedName("global_mod")
    @Ignore
    val globalMod: Boolean? = null,

    @SerializedName("groups")
    @Ignore
    val groups: List<Any?>? = null,

    @SerializedName("has_verified_email")
    @ColumnInfo(name = "has_verified_email")
    val hasVerifiedEmail: Boolean? = null,

    @SerializedName("id")
    @ColumnInfo(name = "id")
    val id: Int,

    @SerializedName("internal_feature")
    @Ignore
    val internalFeature: Boolean? = null,

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

    @SerializedName("is_identity_verified")
    @Ignore
    val isIdentityVerified: Boolean? = null,

    @SerializedName("is_odysee_user")
    @Ignore
    val isOdyseeUser: Boolean? = null,

    @SerializedName("is_reward_approved")
    @Ignore
    val isRewardApproved: Boolean? = null,

    @SerializedName("language")
    @Ignore
    val language: String? = null,

    @SerializedName("latest_claimed_email")
    @Ignore
    val latestClaimedEmail: Any? = null,

    @SerializedName("location")
    @Ignore
    val location: Any? = null,

    @SerializedName("odysee_live_disabled")
    @Ignore
    val odyseeLiveDisabled: Boolean? = null,

    @SerializedName("odysee_live_enabled")
    @Ignore
    val odyseeLiveEnabled: Boolean? = null,

    @SerializedName("password_set")
    @Ignore
    val passwordSet: Boolean? = null,

    @SerializedName("primary_email")
    @Ignore
    val primaryEmail: Any? = null,

    @SerializedName("publish_id")
    @Ignore
    val publishId: Any? = null,

    @SerializedName("updated_at")
    @Ignore
    val updatedAt: String? = null,

    @SerializedName("youtube_channels")
    @Ignore
    val youtubeChannels: List<Any?>? = null
)
