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

package app.newproj.lbrytv.data.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import app.newproj.lbrytv.data.AppDatabase
import app.newproj.lbrytv.data.dto.ClaimSearchRequest
import app.newproj.lbrytv.data.dto.RecommendedContent
import app.newproj.lbrytv.data.dto.Video
import app.newproj.lbrytv.data.entity.ClaimLookup
import app.newproj.lbrytv.data.entity.RemoteKey
import app.newproj.lbrytv.service.LbrynetService
import app.newproj.lbrytv.service.OdyseeService
import retrofit2.HttpException
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

private const val STARTING_PAGE_INDEX = 1
private const val STARTING_SORTING_ORDER = 1

@OptIn(ExperimentalPagingApi::class)
class FeaturedVideosRemoteMediator @Inject constructor(
    private val lbrynetService: LbrynetService,
    private val db: AppDatabase,
    private val odyseeService: OdyseeService,
) : RemoteMediator<Int, Video>() {
    private var primaryContent: RecommendedContent? = null

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Video>): MediatorResult {
        try {
            val page: Int
            var nextSortingOrder: Int
            val remoteKeyLabel = "FEATURED_VIDEOS"
            when (loadType) {
                LoadType.REFRESH -> {
                    page = STARTING_PAGE_INDEX
                    nextSortingOrder = STARTING_SORTING_ORDER
                    val locale = Locale.getDefault()
                    val languageKey = if (locale.language != "en" && locale.country == "BR") {
                        "${locale.language}-${locale.country}"
                    } else {
                        locale.language
                    }
                    primaryContent = odyseeService
                        .recommendedContent()
                        .run {
                            get(languageKey) ?: get("en")
                        }
                        ?.get("PRIMARY_CONTENT")
                }
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = db.remoteKeyDao().remoteKey(remoteKeyLabel)
                    page = remoteKey?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    nextSortingOrder = remoteKey.nextSortingOrder
                }
            }
            val request = ClaimSearchRequest(
                channelIds = primaryContent?.channelIds,
                claimTypes = listOf("stream"),
                streamTypes = listOf("video", "audio"),
                orderBy = listOf("trending_group", "trending_mixed"),
                hasSource = true,
                page = page,
                pageSize = state.config.pageSize,
            )
            val claims = lbrynetService.searchClaims(request).items ?: emptyList()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeyDao().delete(remoteKeyLabel)
                    db.claimLookupDao().deleteAll(remoteKeyLabel)
                }
                db.claimSearchResultDao().upsert(claims)
                val claimLookups = claims.map {
                    ClaimLookup(remoteKeyLabel, it.claimId, nextSortingOrder++)
                }
                db.claimLookupDao().upsert(claimLookups)
                val nextKey = if (claims.isEmpty()) null else page.inc()
                val remoteKey = RemoteKey(remoteKeyLabel, nextKey, nextSortingOrder)
                db.remoteKeyDao().upsert(remoteKey)
            }
            return MediatorResult.Success(endOfPaginationReached = claims.isEmpty())
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }
}
