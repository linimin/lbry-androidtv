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
import app.newproj.lbrytv.data.datasource.VideoLocalDataSource
import app.newproj.lbrytv.data.datasource.VideoRemoteDataSource
import app.newproj.lbrytv.data.dto.Video
import app.newproj.lbrytv.data.paging.ChannelVideosRemoteMediator
import app.newproj.lbrytv.data.paging.FeaturedVideosRemoteMediator
import app.newproj.lbrytv.data.paging.SubscriptionVideosRemoteMediator
import app.newproj.lbrytv.di.LargePageSize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class VideoRepository @Inject constructor(
    private val videoLocalDataSource: VideoLocalDataSource,
    private val videoRemoteDataSource: VideoRemoteDataSource,
    private val featuredVideosRemoteMediator: FeaturedVideosRemoteMediator,
    private val channelVideosMediatorFactory: ChannelVideosRemoteMediator.Factory,
    private val subscriptionVideosMediator: SubscriptionVideosRemoteMediator,
    @LargePageSize private val pagingConfig: PagingConfig,
) {
    fun video(id: String): Flow<Video> = flow {
        videoRemoteDataSource.video(id)?.let {
            videoLocalDataSource.upsert(it)
        }
        emitAll(videoLocalDataSource.video(id))
    }

    fun channelVideos(channelId: String): Flow<PagingData<Video>> = Pager(
        config = pagingConfig,
        remoteMediator = channelVideosMediatorFactory.ChannelVideosRemoteMediator(channelId),
        pagingSourceFactory = { videoLocalDataSource.channelVideoPagingSource(channelId) }
    ).flow

    fun featuredVideos(): Flow<PagingData<Video>> = Pager(
        config = pagingConfig,
        remoteMediator = featuredVideosRemoteMediator,
        pagingSourceFactory = { videoLocalDataSource.featuredVideoPagingSource() }
    ).flow

    fun subscriptionVideos(): Flow<PagingData<Video>> = Pager(
        config = pagingConfig,
        remoteMediator = subscriptionVideosMediator,
        pagingSourceFactory = { videoLocalDataSource.subscriptionVideoPagingSource() }
    ).flow
}
