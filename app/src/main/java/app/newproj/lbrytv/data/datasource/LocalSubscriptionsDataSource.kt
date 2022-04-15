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
import app.newproj.lbrytv.data.entity.Subscription
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalSubscriptionsDataSource @Inject constructor(private val db: AppDatabase) {
    fun subscriptionsFlow(accountName: String): Flow<List<Subscription>> =
        db.subscriptionDao().subscriptionsFlow(accountName)

    suspend fun subscriptions(accountName: String): List<Subscription> =
        db.subscriptionDao().subscriptions(accountName)

    fun subscriptionsPagingSource(accountName: String): PagingSource<Int, Channel> =
        db.channelDao().subscriptions(accountName)

    suspend fun follow(channel: Channel, accountName: String) {
        db.subscriptionDao().upsert(
            Subscription(
                channel.id,
                requireNotNull(channel.claim.permanentUrl),
                isNotificationDisabled = false,
                accountName
            )
        )
    }

    suspend fun unfollow(channel: Channel, accountName: String) {
        db.subscriptionDao().delete(channel.id, accountName)
    }
}
