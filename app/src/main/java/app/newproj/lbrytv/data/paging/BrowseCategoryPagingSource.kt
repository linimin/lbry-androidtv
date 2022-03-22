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

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.BrowseCategory
import app.newproj.lbrytv.data.repo.ChannelRepository
import app.newproj.lbrytv.data.repo.SettingRepository
import app.newproj.lbrytv.data.repo.VideoRepository
import javax.inject.Inject

class BrowseCategoryPagingSource @Inject constructor(
    private val videoRepo: VideoRepository,
    private val channelRepo: ChannelRepository,
    private val settingRepo: SettingRepository,
) : PagingSource<Int, BrowseCategory>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BrowseCategory> {
        return LoadResult.Page(
            data = listOf(
                BrowseCategory(
                    id = R.string.featured.toLong(),
                    nameResId = R.string.featured, iconResId = R.drawable.whatshot,
                    items = videoRepo.featuredVideos()
                ),
                BrowseCategory(
                    id = R.string.subscriptions.toLong(),
                    nameResId = R.string.subscriptions, iconResId = R.drawable.star,
                    items = videoRepo.subscriptionVideos()
                ),
                BrowseCategory(
                    id = R.string.channels.toLong(),
                    nameResId = R.string.channels, iconResId = R.drawable.subscriptions,
                    items = channelRepo.followingChannels()
                ),
                BrowseCategory(
                    id = R.string.settings.toLong(),
                    nameResId = R.string.settings, iconResId = R.drawable.settings,
                    items = settingRepo.settings()
                )
            ),
            prevKey = null, nextKey = null
        )
    }

    override fun getRefreshKey(state: PagingState<Int, BrowseCategory>): Int? = null
}
