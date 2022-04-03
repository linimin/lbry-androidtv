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
import app.newproj.lbrytv.data.AppDatabase
import app.newproj.lbrytv.data.dto.Channel
import app.newproj.lbrytv.data.dto.ClaimSearchResult
import app.newproj.lbrytv.data.entity.Subscription
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChannelLocalDataSource @Inject constructor(
    private val db: AppDatabase,
) {
    fun channel(id: String): Flow<Channel> = db.channelDao().channel(id)

    fun followingChannelPagingSource(accountName: String): PagingSource<Int, Channel> =
        db.channelDao().subscriptions(accountName)

    suspend fun upsert(channel: ClaimSearchResult.Item) {
        db.claimSearchResultDao().upsert(channel)
    }

    suspend fun follow(accountName: String, channel: Channel) {
        channel.claim.permanentUrl?.let { permanentUrl ->
            db.subscriptionDao().upsert(
                Subscription(channel.id, permanentUrl, false, accountName)
            )
        }
    }

    suspend fun unfollow(accountName: String, channel: Channel) {
        db.subscriptionDao().delete(channel.id, accountName)
    }
}
