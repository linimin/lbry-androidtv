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
import app.newproj.lbrytv.data.dto.RelatedClaim
import app.newproj.lbrytv.service.LighthouseService
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

private const val STARTING_PAGE_INDEX = 0

class RelatedClaimPagingSource @AssistedInject constructor(
    @Assisted private val query: String,
    private val lighthouseService: LighthouseService,
) : PagingSource<Int, RelatedClaim>() {
    @AssistedFactory
    interface Factory {
        fun RelatedClaimPagingSource(query: String): RelatedClaimPagingSource
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RelatedClaim> {
        val page = params.key ?: STARTING_PAGE_INDEX
        val relatedClaims = lighthouseService.search(
            query,
            true,
            params.loadSize,
            page
        )
        return LoadResult.Page(
            relatedClaims,
            null,
            if (relatedClaims.isEmpty()) null else page.inc()
        )
    }

    override fun getRefreshKey(state: PagingState<Int, RelatedClaim>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}