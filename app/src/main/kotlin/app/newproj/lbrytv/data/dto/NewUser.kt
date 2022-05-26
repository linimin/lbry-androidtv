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

data class NewUser(
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
    val invitedById: String? = null,

    @SerializedName("invited_at")
    @Ignore
    val invitedAt: String? = null,

    @SerializedName("invites_remaining")
    @Ignore
    val invitesRemaining: Int? = null,

    @SerializedName("invite_reward_claimed")
    @Ignore
    val inviteRewardClaimed: Boolean? = null,

    @SerializedName("is_reward_approved")
    @Ignore
    val isRewardApproved: Boolean? = null,

    @SerializedName("is_email_enabled")
    @Ignore
    val isEmailEnabled: Boolean? = null,

    @SerializedName("manual_approval_user_id")
    @Ignore
    val manualApprovalUserId: String? = null,

    @SerializedName("reward_status_change_trigger")
    @Ignore
    val rewardStatusChangeTrigger: Any? = null,

    @SerializedName("settings")
    @Ignore
    val settings: Any? = null,

    @SerializedName("prev_settings")
    @Ignore
    val prevSettings: Any? = null,

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

    @SerializedName("auth_token")
    @Ignore
    val plainTextAuthToken: String? = null,
)
