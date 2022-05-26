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

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.newproj.lbrytv.data.datasource.LbrySubscriptionsDataSource
import app.newproj.lbrytv.data.datasource.LocalSubscriptionsDataSource
import app.newproj.lbrytv.data.datasource.OdyseeSubscriptionsDataSource
import app.newproj.lbrytv.data.dto.Channel
import app.newproj.lbrytv.data.entity.Subscription
import app.newproj.lbrytv.data.remotemediator.SubscriptionsRemoteMediator
import app.newproj.lbrytv.di.SmallPageSize
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubscriptionsRepository @Inject constructor(
    private val localSubscriptionsDataSource: LocalSubscriptionsDataSource,
    private val lbrySubscriptionsDataSource: LbrySubscriptionsDataSource,
    private val odyseeSubscriptionsDataSource: OdyseeSubscriptionsDataSource,
    private val subscriptionsRemoteMediatorFactory: SubscriptionsRemoteMediator.Factory,
    @SmallPageSize private val pagingConfig: PagingConfig,
) {
    fun subscriptionsFlow(accountName: String): Flow<List<Subscription>> =
        localSubscriptionsDataSource.subscriptionsFlow(accountName)

    @OptIn(ExperimentalPagingApi::class)
    fun subscriptions(accountName: String): Flow<PagingData<Channel>> = Pager(
        config = pagingConfig,
        remoteMediator = subscriptionsRemoteMediatorFactory
            .SubscriptionsRemoteMediator(accountName),
        pagingSourceFactory = { localSubscriptionsDataSource.subscriptionsPagingSource(accountName) }
    ).flow

    suspend fun follow(channel: Channel, accountName: String) {
        localSubscriptionsDataSource.follow(channel, accountName)
        lbrySubscriptionsDataSource.follow(channel)
        odyseeSubscriptionsDataSource.follow(channel)
    }

    suspend fun unfollow(channel: Channel, accountName: String) {
        localSubscriptionsDataSource.unfollow(channel, accountName)
        lbrySubscriptionsDataSource.unfollow(channel)
        odyseeSubscriptionsDataSource.unfollow(channel)
    }
}
