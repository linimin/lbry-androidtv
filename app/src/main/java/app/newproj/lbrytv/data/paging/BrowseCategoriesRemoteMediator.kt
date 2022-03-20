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

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.AppDatabase
import app.newproj.lbrytv.data.entity.BrowseCategory
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class BrowseCategoriesRemoteMediator @Inject constructor(
    private val db: AppDatabase,
) : RemoteMediator<Int, BrowseCategory>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BrowseCategory>,
    ): MediatorResult {
        return try {
            if (loadType == LoadType.REFRESH) {
                db.withTransaction {
                    db.browseCategoryDao().clear()
                    var sortingOrder = 0
                    val browseCategories = listOf(
                        BrowseCategory(
                            R.string.trending.toLong(),
                            R.string.trending,
                            R.drawable.whatshot,
                            sortingOrder++
                        ),
                        BrowseCategory(
                            R.string.subscriptions.toLong(),
                            R.string.subscriptions,
                            R.drawable.star,
                            sortingOrder++
                        ),
                        BrowseCategory(
                            R.string.watch_history.toLong(),
                            R.string.watch_history,
                            R.drawable.history,
                            sortingOrder++
                        ),
                        BrowseCategory(
                            R.string.channels.toLong(),
                            R.string.channels,
                            R.drawable.subscriptions,
                            sortingOrder++
                        ),
                        BrowseCategory(
                            R.string.settings.toLong(),
                            R.string.settings,
                            R.drawable.settings,
                            sortingOrder
                        ),
                    )
                    db.browseCategoryDao().upsert(browseCategories)
                }
            }
            MediatorResult.Success(endOfPaginationReached = true)
        } catch (error: IOException) {
            MediatorResult.Error(error)
        }
    }
}
