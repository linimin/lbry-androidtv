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
import app.newproj.lbrytv.data.datasource.ChannelRemoteDataSource
import app.newproj.lbrytv.data.dto.Channel
import app.newproj.lbrytv.data.paging.SubscriptionRemoteMediator
import app.newproj.lbrytv.di.LargePageSize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class ChannelRepository @Inject constructor(
    private val channelLocalDataSource: ChannelLocalDataSource,
    private val channelRemoteDataSource: ChannelRemoteDataSource,
    private val subscriptionRemoteMediator: SubscriptionRemoteMediator,
    @LargePageSize private val pagingConfig: PagingConfig,
) {
    fun channel(id: String): Flow<Channel> = flow {
        channelRemoteDataSource.channel(id)?.let {
            channelLocalDataSource.upsert(it)
        }
        emitAll(channelLocalDataSource.channel(id))
    }

    fun followingChannels(): Flow<PagingData<Channel>> = Pager(
        config = pagingConfig,
        remoteMediator = subscriptionRemoteMediator,
        pagingSourceFactory = { channelLocalDataSource.followingChannelPagingSource() }
    ).flow

    suspend fun follow(channel: Channel) {
        channelLocalDataSource.follow(channel)
        channel.claim.permanentUrl?.let {
            channelRemoteDataSource.follow(it)
        }
    }

    suspend fun unfollow(channel: Channel) {
        channelLocalDataSource.unfollow(channel)
        channel.claim.permanentUrl?.let {
            channelRemoteDataSource.unfollow(it)
        }
    }
}
