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

package app.newproj.lbrytv.data.repo

import androidx.paging.PagingData
import androidx.paging.map
import app.newproj.lbrytv.data.dto.ClaimLookupLabel
import app.newproj.lbrytv.data.dto.ClaimSearchRequest
import app.newproj.lbrytv.data.dto.LbryUri
import app.newproj.lbrytv.data.dto.Video
import app.newproj.lbrytv.service.LbrynetService
import app.newproj.lbrytv.service.OdyseeService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val claimRepo: ClaimRepository,
    private val odyseeService: OdyseeService,
    private val lbrynetService: LbrynetService,
) {
    fun video(id: String): Flow<Video> = claimRepo.claim(id).map { Video(it) }

    fun channelVideos(channelId: String): Flow<PagingData<Video>> = claimRepo.claims(
        channelId,
        ClaimSearchRequest(
            channelIds = listOf(channelId),
            claimTypes = listOf("stream", "repost"),
            streamTypes = listOf("video"),
            orderBy = listOf("release_time"),
            hasSource = true,
        )
    ).map { pagingData -> pagingData.map { Video(it) } }

    suspend fun featuredVideos(): Flow<PagingData<Video>>? {
        val primaryContent = odyseeService.content()["en"]?.get("PRIMARY_CONTENT") ?: return null
        return claimRepo.claims(
            ClaimLookupLabel.FEATURED_VIDEOS.name,
            ClaimSearchRequest(
                channelIds = primaryContent?.channelIds,
                claimTypes = listOf("stream", "repost"),
                streamTypes = listOf("video"),
                orderBy = listOf("trending_group", "trending_mixed"),
                hasSource = true,
            )
        ).map { pagingData -> pagingData.map { Video(it) } }
    }

    suspend fun subscriptionVideos(): Flow<PagingData<Video>>? {
        val subscriptionChannelIds = try {
            lbrynetService.preference().shared?.value?.subscriptions?.mapNotNull {
                LbryUri.parse(LbryUri.normalize(it)).channelClaimId
            } ?: return null
        } catch (e: Throwable) {
            return null
        }
        return claimRepo.claims(
            ClaimLookupLabel.SUBSCRIPTION_VIDEOS.name,
            ClaimSearchRequest(
                channelIds = subscriptionChannelIds,
                claimTypes = listOf("stream", "repost"),
                streamTypes = listOf("video"),
                orderBy = listOf("trending_group", "trending_mixed"),
                hasSource = true,
            )
        ).map { pagingData -> pagingData.map { Video(it) } }
    }
}
