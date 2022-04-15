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
import app.newproj.lbrytv.data.repo.SettingsRepository
import app.newproj.lbrytv.data.repo.SubscriptionRepository
import app.newproj.lbrytv.data.repo.VideosRepository
import javax.inject.Inject

class BrowseCategoryPagingSource @Inject constructor(
    private val accountRepository: AccountRepository,
    private val videosRepository: VideosRepository,
    private val settingsRepository: SettingsRepository,
    private val subscriptionRepository: SubscriptionRepository,
) : PagingSource<Int, BrowseCategory>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BrowseCategory> {
        val categories = mutableListOf<BrowseCategory>()
        categories.add(
            BrowseCategory(
                id = R.id.browse_category_featured.toLong(),
                name = R.string.featured, icon = R.drawable.whatshot,
                items = videosRepository.featuredVideos()
            )
        )
        val account = accountRepository.currentAccount()
        if (account != null) {
            categories.add(
                BrowseCategory(
                    id = R.id.browse_category_subscriptions.toLong(),
                    name = R.string.subscriptions, icon = R.drawable.star,
                    items = videosRepository.subscriptionVideos()
                )
            )
            categories.add(
                BrowseCategory(
                    id = R.id.browse_category_channels.toLong(),
                    name = R.string.channels, icon = R.drawable.subscriptions,
                    items = subscriptionRepository.subscriptions(account.name)
                )
            )
        }
        categories.add(
            BrowseCategory(
                id = R.id.browse_category_settings.toLong(),
                name = R.string.settings, icon = R.drawable.settings,
                items = settingsRepository.settings()
            )
        )
        return LoadResult.Page(data = categories, prevKey = null, nextKey = null)
    }

    override fun getRefreshKey(state: PagingState<Int, BrowseCategory>): Int? = null
}
