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

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import java.time.Instant

data class RelatedClaim(
    @SerializedName("channel")
    @ColumnInfo(name = "channel_name")
    val channel: String? = null,

    @SerializedName("channel_claim_id")
    @Ignore
    val channelClaimId: String? = null,

    @SerializedName("claimId")
    @ColumnInfo(name = "id")
    val id: String,

    @SerializedName("duration")
    @ColumnInfo(name = "duration")
    val duration: Long? = null,

    @SerializedName("fee")
    @Ignore
    val fee: Long? = null,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String? = null,

    @SerializedName("release_time")
    @ColumnInfo(name = "release_time")
    val releaseTime: Instant? = null,

    @SerializedName("thumbnail_url")
    @ColumnInfo(name = "thumbnail")
    val thumbnailUrl: Uri? = null,

    @SerializedName("title")
    @ColumnInfo(name = "title")
    val title: String? = null,
)
