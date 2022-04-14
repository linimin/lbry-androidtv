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

package app.newproj.lbrytv.data.datasource

import app.newproj.lbrytv.data.dto.ClaimSearchRequest
import app.newproj.lbrytv.data.dto.ClaimSearchResult
import app.newproj.lbrytv.data.dto.LbryUri
import app.newproj.lbrytv.service.LbrynetService
import app.newproj.lbrytv.service.OdyseeService
import java.util.Locale
import javax.inject.Inject

class VideoRemoteDataSource @Inject constructor(
    private val lbrynetService: LbrynetService,
    private val odyseeService: OdyseeService,
) {
    suspend fun video(id: String): ClaimSearchResult.Item? =
        lbrynetService.searchClaims(ClaimSearchRequest(claimId = id))
            .items?.firstOrNull()

    suspend fun featuredVideos(): List<ClaimSearchResult.Item> {
        val locale = Locale.getDefault()
        val languageKey = if (locale.language != "en" && locale.country == "BR") {
            "${locale.language}-${locale.country}"
        } else {
            locale.language
        }
        val channelIds = odyseeService
            .content()
            .run {
                get(languageKey) ?: get("en")
            }
            ?.get("PRIMARY_CONTENT")
            ?.channelIds
        val request = ClaimSearchRequest(
            channelIds = channelIds,
            claimTypes = listOf("stream"),
            streamTypes = listOf("video"),
            orderBy = listOf("trending_group", "trending_mixed"),
            hasSource = true,
            page = 1,
            pageSize = 20,
        )
        return lbrynetService.searchClaims(request).items ?: emptyList()
    }

    suspend fun subscriptionVideos(): List<ClaimSearchResult.Item> {
        val subscriptionChannelIds =
            lbrynetService.preference().shared?.value?.following
                ?.mapNotNull {
                    val lbryUri = LbryUri.parse(it.uri.toString())
                    lbryUri.channelClaimId
                }?.takeIf { it.isNotEmpty() }
                ?: return emptyList()
        val request = ClaimSearchRequest(
            channelIds = subscriptionChannelIds,
            claimTypes = listOf("stream"),
            streamTypes = listOf("video"),
            orderBy = listOf("release_time"),
            hasSource = true,
            page = 1,
            pageSize = 20,
        )
        return lbrynetService.searchClaims(request).items ?: emptyList()
    }
}
