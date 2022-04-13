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

package app.newproj.lbrytv.data.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import app.newproj.lbrytv.data.dto.ClaimType
import java.time.Instant

/**
 * A stake that contains metadata about a stream or channel.
 */
@Entity(tableName = "claim")
data class Claim(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "thumbnail")
    val thumbnail: Uri?,

    @ColumnInfo(name = "title")
    val title: String?,

    @ColumnInfo(name = "effective_amount")
    val effectiveAmount: String?,

    @ColumnInfo(name = "trending_group")
    val trendingGroup: Int?,

    @ColumnInfo(name = "trending_mixed")
    val trendingMixed: Double?,

    @ColumnInfo(name = "release_time")
    val releaseTime: Instant?,

    @ColumnInfo(name = "channel_name")
    val channelName: String?,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "value_type")
    val valueType: ClaimType?,

    @ColumnInfo(name = "permanent_url")
    val permanentUrl: Uri? = null,

    @ColumnInfo(name = "short_url")
    val shortUrl: Uri? = null,

    /**
     * A UTF-8 string of up to 255 bytes used to address the claim.
     */
    @ColumnInfo(name = "name")
    val name: String? = null,

    @ColumnInfo(name = "cover")
    val cover: Uri? = null,

    @ColumnInfo(name = "channel_id")
    val channelId: String? = null,

    @ColumnInfo(name = "duration")
    val duration: Long? = null,
)

fun Claim.streamingUrl(): Uri =
    Uri.parse("https://cdn.lbryplayer.xyz/content/claims/$name/$id/stream")
