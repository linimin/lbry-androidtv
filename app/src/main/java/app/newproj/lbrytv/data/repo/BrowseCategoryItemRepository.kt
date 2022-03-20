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

import androidx.paging.PagingData
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.BrowseCategoryItem
import app.newproj.lbrytv.data.entity.BrowseCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class BrowseCategoryItemRepository @Inject constructor(
    private val videoRepo: VideoRepository,
    private val channelRepo: ChannelRepository,
    private val settingRepo: SettingRepository,
    private val accountRepo: AccountRepository,
) {
    suspend fun items(category: BrowseCategory): Flow<PagingData<out BrowseCategoryItem>> =
        when (category.nameResId) {
            R.string.trending -> videoRepo.trendingVideos()
            R.string.subscriptions -> videoRepo.subscriptionVideos()
            R.string.watch_history -> accountRepo.currentAccount()?.let { account ->
                videoRepo.watchHistory(account)
            } ?: emptyFlow()

            R.string.channels -> channelRepo.subscriptionChannels()
            R.string.settings -> settingRepo.settings()
            else -> emptyFlow()
        }
}
