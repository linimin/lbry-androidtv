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
import app.newproj.lbrytv.data.dto.BrowseCategory
import app.newproj.lbrytv.data.paging.BrowseCategoryPagingSource
import app.newproj.lbrytv.di.SmallPageSize
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Provider

@OptIn(ExperimentalPagingApi::class)
class BrowseCategoryRepository @Inject constructor(
    @SmallPageSize private val pagingConfig: PagingConfig,
    private val browseCategoryPagingSourceProvider: Provider<BrowseCategoryPagingSource>,
) {
    fun browseCategories(): Flow<PagingData<BrowseCategory>> = Pager(
        config = pagingConfig,
        pagingSourceFactory = browseCategoryPagingSourceProvider::get
    ).flow
}
