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
import app.newproj.lbrytv.data.dto.Channel
import app.newproj.lbrytv.data.dto.ClaimSearchRequest
import app.newproj.lbrytv.data.dto.LbryUri
import app.newproj.lbrytv.data.entity.Subscription
import app.newproj.lbrytv.service.LbrynetService
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class SubscriptionRemoteMediator @AssistedInject constructor(
    @Assisted private val accountName: String,
    private val lbrynetService: LbrynetService,
    private val db: AppDatabase,
) : RemoteMediator<Int, Channel>() {
    @AssistedFactory
    interface Factory {
        fun SubscriptionRemoteMediator(accountName: String): SubscriptionRemoteMediator
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Channel>,
    ): MediatorResult {
        try {
            if (loadType == LoadType.REFRESH) {
                val subscriptions =
                    lbrynetService.preference().shared?.value
                        ?.following
                        ?.mapNotNull { following ->
                            val lbryUri = LbryUri.parse(following.uri.toString())
                            val claimId = lbryUri.claimId ?: return@mapNotNull null
                            val uri = following.uri ?: return@mapNotNull null
                            val notificationsDisabled = following.notificationsDisabled ?: false
                            Subscription(claimId, uri, notificationsDisabled, accountName)
                        } ?: emptyList()
                val claims = if (subscriptions.isNotEmpty()) {
                    lbrynetService.searchClaims(
                        ClaimSearchRequest(claimIds = subscriptions.map { it.claimId })
                    ).items ?: emptyList()
                } else {
                    emptyList()
                }
                db.withTransaction {
                    db.claimSearchResultDao().upsert(claims)
                    db.subscriptionDao().upsert(subscriptions)
                }
            }
            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (error: HttpException) {
            return MediatorResult.Error(error)
        } catch (error: IOException) {
            return MediatorResult.Error(error)
        }
    }
}
