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


import com.google.gson.annotations.SerializedName

data class SignInResponse(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("language") val language: String? = null,
    @SerializedName("given_name") val givenName: Any? = null,
    @SerializedName("family_name") val familyName: Any? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("invited_by_id") val invitedById: Int? = null,
    @SerializedName("invited_at") val invitedAt: String? = null,
    @SerializedName("invites_remaining") val invitesRemaining: Int? = null,
    @SerializedName("invite_reward_claimed") val inviteRewardClaimed: Boolean? = null,
    @SerializedName("is_email_enabled") val isEmailEnabled: Boolean? = null,
    @SerializedName("publish_id") val publishId: Int? = null,
    @SerializedName("country") val country: Any? = null,
    @SerializedName("is_odysee_user") val isOdyseeUser: Boolean? = null,
    @SerializedName("location") val location: Any? = null,
    @SerializedName("youtube_channels") val youtubeChannels: List<Any?>? = null,
    @SerializedName("primary_email") val primaryEmail: String? = null,
    @SerializedName("password_set") val passwordSet: Boolean? = null,
    @SerializedName("latest_claimed_email") val latestClaimedEmail: Any? = null,
    @SerializedName("has_verified_email") val hasVerifiedEmail: Boolean? = null,
    @SerializedName("is_identity_verified") val isIdentityVerified: Boolean? = null,
    @SerializedName("is_reward_approved") val isRewardApproved: Boolean? = null,
    @SerializedName("groups") val groups: List<Any?>? = null,
    @SerializedName("device_types") val deviceTypes: List<String?>? = null,
    @SerializedName("odysee_live_enabled") val odyseeLiveEnabled: Boolean? = null,
    @SerializedName("odysee_live_disabled") val odyseeLiveDisabled: Boolean? = null,
    @SerializedName("global_mod") val globalMod: Boolean? = null,
    @SerializedName("experimental_ui") val experimentalUi: Boolean? = null,
    @SerializedName("internal_feature") val internalFeature: Boolean? = null,
    @SerializedName("odysee_member") val odyseeMember: Boolean? = null,
)
