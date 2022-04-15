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
import app.newproj.lbrytv.data.datasource.ChannelLocalDataSource
import app.newproj.lbrytv.data.datasource.LbrySubscriptionDataSource
import app.newproj.lbrytv.data.datasource.OdyseeSubscriptionDataSource
import app.newproj.lbrytv.data.dto.Channel
import app.newproj.lbrytv.data.remotemediator.SubscriptionRemoteMediator
import app.newproj.lbrytv.di.LargePageSize
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class SubscriptionRepository @Inject constructor(
    private val lbrySubscriptionDataSource: LbrySubscriptionDataSource,
    private val odyseeSubscriptionDataSource: OdyseeSubscriptionDataSource,
    private val channelLocalDataSource: ChannelLocalDataSource,
    private val subscriptionRemoteMediatorFactory: SubscriptionRemoteMediator.Factory,
    @LargePageSize private val pagingConfig: PagingConfig,
) {
    suspend fun follow(channel: Channel, accountName: String) {
        channelLocalDataSource.follow(accountName, channel)
        channel.claim.channelName?.let {
            lbrySubscriptionDataSource.follow(channel.id, it)
        }
        channel.claim.permanentUrl?.let {
            odyseeSubscriptionDataSource.follow(it)
        }
    }

    suspend fun unfollow(channel: Channel, accountName: String) {
        channelLocalDataSource.unfollow(accountName, channel)
        lbrySubscriptionDataSource.unfollow(channel.id)
        channel.claim.permanentUrl?.let {
            odyseeSubscriptionDataSource.unfollow(it)
        }
    }

    fun subscriptions(accountName: String): Flow<PagingData<Channel>> = Pager(
        config = pagingConfig,
        remoteMediator = subscriptionRemoteMediatorFactory.SubscriptionRemoteMediator(accountName),
        pagingSourceFactory = { channelLocalDataSource.followingChannelPagingSource(accountName) }
    ).flow
}
