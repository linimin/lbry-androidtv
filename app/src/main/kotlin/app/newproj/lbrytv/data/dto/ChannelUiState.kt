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
import app.newproj.lbrytv.data.entity.Claim

data class ChannelUiState(
    override val id: String,
    val thumbnail: Uri?,
    val title: String?,
    val name: String?,
    val isFollowing: Boolean = false,
) : BrowseItemUiState {
    companion object {
        fun fromChannel(channel: Channel) = ChannelUiState(
            channel.id,
            channel.claim.thumbnail,
            channel.claim.title,
            channel.claim.name,
            channel.isFollowing,
        )

        fun fromRelatedClaim(claim: RelatedClaim) = ChannelUiState(
            claim.id,
            claim.thumbnailUrl,
            claim.title,
            claim.name
        )

        fun fromClaim(claim: Claim) = ChannelUiState(
            claim.id,
            claim.thumbnail,
            claim.title,
            claim.name
        )
    }
}
