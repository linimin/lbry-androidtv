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

import app.newproj.lbrytv.data.AppDatabase
import app.newproj.lbrytv.di.ApplicationCoroutineScope
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

private const val STARTING_SORTING_ORDER = 1

class WatchHistoryRepository @Inject constructor(
    private val db: AppDatabase,
    private val accountRepo: AccountRepository,
    @ApplicationCoroutineScope private val externalScope: CoroutineScope,
) {
//    suspend fun add(video: Video) {
//        externalScope.launch {
//            accountRepo.currentAccount()?.name?.let { accountName ->
//                db.withTransaction {
//                    val remoteKey = db.remoteKeyDao().remoteKey(accountName)
//                    var nextSortingOrder = remoteKey?.nextSortingOrder ?: STARTING_SORTING_ORDER
//                    db.claimLookupDao().delete(accountName, video.id)
//                    db.claimLookupDao().insert(
//                        ClaimLookup(accountName, video.id, nextSortingOrder++)
//                    )
//                    db.remoteKeyDao().upsert(
//                        RemoteKey(accountName, null, nextSortingOrder)
//                    )
//                }
//            }
//        }.join()
//    }
//
//    suspend fun clear() {
//        externalScope.launch {
//            accountRepo.currentAccount()?.name?.let { accountName ->
//                db.withTransaction {
//                    db.remoteKeyDao().delete(accountName)
//                    db.claimLookupDao().deleteAll(accountName)
//                }
//            }
//        }.join()
//    }
}
