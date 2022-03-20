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

package app.newproj.lbrytv.data.paging

import app.newproj.lbrytv.data.AppDatabase
import app.newproj.lbrytv.data.dto.ClaimLookupLabel
import app.newproj.lbrytv.data.dto.ClaimSearchRequest
import app.newproj.lbrytv.data.dto.LbryUri
import app.newproj.lbrytv.di.LbrynetProxyService
import app.newproj.lbrytv.service.LbrynetService
import javax.inject.Inject

class SubscriptionVideosRemoteMediator @Inject constructor(
    @LbrynetProxyService private val lbrynetService: LbrynetService,
    db: AppDatabase,
) : SearchClaimsRemoteMediator(lbrynetService, db) {

    override val label: String = ClaimLookupLabel.SUBSCRIPTION_VIDEOS.name

    override suspend fun onCreateInitialRequest(): ClaimSearchRequest? {
        val channelIds = lbrynetService.preference()
            .shared
            ?.value
            ?.subscriptions
            ?.mapNotNull { LbryUri.parse(it).channelClaimId }
            ?.takeIf { it.isNotEmpty() }
            ?: return null
        return ClaimSearchRequest(
            channelIds = channelIds,
            claimTypes = listOf("stream", "repost"),
            streamTypes = listOf("video"),
            orderBy = listOf("release_time"),
            hasSource = true,
        )
    }
}
