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

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.BrowseCategory
import app.newproj.lbrytv.data.repo.AccountRepository
import app.newproj.lbrytv.data.repo.ChannelsRepository
import app.newproj.lbrytv.data.repo.SettingsRepository
import app.newproj.lbrytv.data.repo.VideosRepository
import javax.inject.Inject

class BrowseCategoryPagingSource @Inject constructor(
    private val accountRepository: AccountRepository,
    private val videosRepo: VideosRepository,
    private val channelsRepo: ChannelsRepository,
    private val settingsRepo: SettingsRepository,
) : PagingSource<Int, BrowseCategory>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BrowseCategory> {
        val account = accountRepository.currentAccount()
        val categories = mutableListOf<BrowseCategory>()
        categories.add(
            BrowseCategory(
                id = R.string.featured.toLong(),
                name = R.string.featured, icon = R.drawable.whatshot,
                items = videosRepo.featuredVideos()
            )
        )
        if (account != null) {
            categories.add(
                BrowseCategory(
                    id = R.string.subscriptions.toLong(),
                    name = R.string.subscriptions, icon = R.drawable.star,
                    items = videosRepo.subscriptionVideos()
                )
            )
            categories.add(
                BrowseCategory(
                    id = R.string.channels.toLong(),
                    name = R.string.channels, icon = R.drawable.subscriptions,
                    items = channelsRepo.followingChannels(account.name)
                )
            )
        }
        categories.add(
            BrowseCategory(
                id = R.string.settings.toLong(),
                name = R.string.settings, icon = R.drawable.settings,
                items = settingsRepo.settings()
            )
        )
        return LoadResult.Page(data = categories, prevKey = null, nextKey = null)
    }

    override fun getRefreshKey(state: PagingState<Int, BrowseCategory>): Int? = null
}