/*
 * MIT License
 *
 * Copyright (c) 2022 LIN I MIN
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package app.newproj.lbrytv.data.dto

import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName

data class TokenUser(
    @SerializedName("id")
    @ColumnInfo(name = "id")
    val id: Int,

    @SerializedName("language")
    @Ignore
    val language: String? = null,

    @SerializedName("given_name")
    @Ignore
    val givenName: Any? = null,

    @SerializedName("family_name")
    @Ignore
    val familyName: Any? = null,

    @SerializedName("created_at")
    @Ignore
    val createdAt: String? = null,

    @SerializedName("updated_at")
    @Ignore
    val updatedAt: String? = null,

    @SerializedName("invited_by_id")
    @Ignore
    val invitedById: Any? = null,

    @SerializedName("invited_at")
    @Ignore
    val invitedAt: Any? = null,

    @SerializedName("invites_remaining")
    @Ignore
    val invitesRemaining: Int? = null,

    @SerializedName("invite_reward_claimed")
    @Ignore
    val inviteRewardClaimed: Boolean? = null,

    @SerializedName("is_email_enabled")
    @Ignore
    val isEmailEnabled: Boolean? = null,

    @SerializedName("publish_id")
    @Ignore
    val publishId: Any? = null,

    @SerializedName("country")
    @Ignore
    val country: Any? = null,

    @SerializedName("is_odysee_user")
    @Ignore
    val isOdyseeUser: Boolean? = null,

    @SerializedName("location")
    @Ignore
    val location: Any? = null,

    @SerializedName("youtube_channels")
    @Ignore
    val youtubeChannels: List<Any?>? = null,

    @SerializedName("primary_email")
    @Ignore
    val primaryEmail: Any? = null,

    @SerializedName("password_set")
    @Ignore
    val passwordSet: Boolean? = null,

    @SerializedName("latest_claimed_email")
    @Ignore
    val latestClaimedEmail: Any? = null,

    @SerializedName("has_verified_email")
    @ColumnInfo(name = "has_verified_email")
    val hasVerifiedEmail: Boolean? = null,

    @SerializedName("is_identity_verified")
    @Ignore
    val isIdentityVerified: Boolean? = null,

    @SerializedName("is_reward_approved")
    @Ignore
    val isRewardApproved: Boolean? = null,

    @SerializedName("groups")
    @Ignore
    val groups: List<Any?>? = null,

    @SerializedName("device_types")
    @Ignore
    val deviceTypes: List<Any?>? = null,

    @SerializedName("odysee_live_enabled")
    @Ignore
    val odyseeLiveEnabled: Boolean? = null,

    @SerializedName("odysee_live_disabled")
    @Ignore
    val odyseeLiveDisabled: Boolean? = null,

    @SerializedName("global_mod")
    @Ignore
    val globalMod: Boolean? = null,

    @SerializedName("experimental_ui")
    @Ignore
    val experimentalUi: Boolean? = null,

    @SerializedName("internal_feature")
    @Ignore
    val internalFeature: Boolean? = null,
)
