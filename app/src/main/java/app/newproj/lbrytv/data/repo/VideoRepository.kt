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

import android.accounts.Account
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import app.newproj.lbrytv.data.AppDatabase
import app.newproj.lbrytv.data.dto.Video
import app.newproj.lbrytv.data.paging.ChannelVideosRemoteMediator
import app.newproj.lbrytv.data.paging.SubscriptionVideosRemoteMediator
import app.newproj.lbrytv.data.paging.TrendingVideosRemoteMediator
import app.newproj.lbrytv.di.LargePageSize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class VideoRepository @Inject constructor(
    private val db: AppDatabase,
    @LargePageSize private val pagingConfig: PagingConfig,
    private val trendingVideosMediator: TrendingVideosRemoteMediator,
    private val subscriptionVideosMediator: SubscriptionVideosRemoteMediator,
    private val channelVideosMediatorFactory: ChannelVideosRemoteMediator.Factory,
) {
    fun video(id: String): Flow<Video> = db.claimDao().claim(id).map { Video(it) }

    fun trendingVideos(): Flow<PagingData<Video>> = Pager(
        config = pagingConfig,
        remoteMediator = trendingVideosMediator,
        pagingSourceFactory = {
            db.claimDao().claimsAscendingSorted(trendingVideosMediator.label)
        }
    ).flow.map { pagingData -> pagingData.map { Video(it) } }

    fun subscriptionVideos(): Flow<PagingData<Video>> = Pager(
        config = pagingConfig,
        remoteMediator = subscriptionVideosMediator,
        pagingSourceFactory = {
            db.claimDao().claimsAscendingSorted(subscriptionVideosMediator.label)
        }
    ).flow.map { pagingData -> pagingData.map { Video(it) } }

    fun channelVideos(channelId: String): Flow<PagingData<Video>> =
        channelVideosMediatorFactory.ChannelVideosRemoteMediator(channelId).let { mediator ->
            Pager(
                config = pagingConfig,
                remoteMediator = mediator,
                pagingSourceFactory = {
                    db.claimDao().claimsAscendingSorted(mediator.label)
                }
            )
        }.flow.map { pagingData -> pagingData.map { Video(it) } }

    fun watchHistory(account: Account): Flow<PagingData<Video>> = Pager(
        config = pagingConfig,
        pagingSourceFactory = {
            db.claimDao().claimsDescendingSorted(account.name)
        }
    ).flow.map { pagingData -> pagingData.map { Video(it) } }
}
