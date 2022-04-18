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
import app.newproj.lbrytv.data.datasource.LocalSubscriptionsDataSource
import app.newproj.lbrytv.data.dto.ClaimSearchRequest
import app.newproj.lbrytv.data.dto.Video
import app.newproj.lbrytv.data.entity.ClaimLookup
import app.newproj.lbrytv.data.entity.RemoteKey
import app.newproj.lbrytv.service.LbrynetService
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1
private const val STARTING_SORTING_ORDER = 1

@OptIn(ExperimentalPagingApi::class)
class SubscriptionVideosRemoteMediator @AssistedInject constructor(
    @Assisted private val accountName: String,
    private val lbrynetService: LbrynetService,
    private val localSubscriptionsDataSource: LocalSubscriptionsDataSource,
    private val appDatabase: AppDatabase,
) : RemoteMediator<Int, Video>() {
    @AssistedFactory
    interface Factory {
        fun SubscriptionVideosRemoteMediator(accountName: String): SubscriptionVideosRemoteMediator
    }

    private var subscriptionChannelIds = emptyList<String>()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Video>): MediatorResult {
        try {
            val page: Int
            var nextSortingOrder: Int
            val remoteKeyLabel = "SUBSCRIPTION_VIDEOS"
            when (loadType) {
                LoadType.REFRESH -> {
                    page = STARTING_PAGE_INDEX
                    nextSortingOrder = STARTING_SORTING_ORDER
                    subscriptionChannelIds = localSubscriptionsDataSource
                        .subscriptions(accountName)
                        .map { it.claimId }
                }
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = appDatabase.remoteKeyDao().remoteKey(remoteKeyLabel)
                    page = remoteKey?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    nextSortingOrder = remoteKey.nextSortingOrder
                }
            }
            val claims = if (subscriptionChannelIds.isNotEmpty()) {
                lbrynetService.searchClaims(
                    request = ClaimSearchRequest(
                        channelIds = subscriptionChannelIds,
                        claimTypes = listOf("stream"),
                        streamTypes = listOf("video"),
                        orderBy = listOf("release_time"),
                        hasSource = true,
                        page = page,
                        pageSize = state.config.pageSize,
                    )
                ).items ?: emptyList()
            } else {
                emptyList()
            }
            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    appDatabase.remoteKeyDao().delete(remoteKeyLabel)
                    appDatabase.claimLookupDao().deleteAll(remoteKeyLabel)
                }
                appDatabase.claimSearchResultDao().upsert(claims)
                val claimLookups = claims.map {
                    ClaimLookup(remoteKeyLabel, it.claimId, nextSortingOrder++)
                }
                appDatabase.claimLookupDao().upsert(claimLookups)
                val nextKey = if (claims.isEmpty()) null else page.inc()
                val remoteKey = RemoteKey(remoteKeyLabel, nextKey, nextSortingOrder)
                appDatabase.remoteKeyDao().upsert(remoteKey)
            }
            return MediatorResult.Success(endOfPaginationReached = claims.isEmpty())
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }
}
