package app.newproj.lbrytv.data.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.newproj.lbrytv.data.dto.RelatedClaim
import app.newproj.lbrytv.service.LighthouseService
import javax.inject.Inject

class SearchPagingSource private constructor(
    private val query: String,
    private val service: LighthouseService,
) : PagingSource<Int, RelatedClaim>() {

    class Factory @Inject constructor(private val service: LighthouseService) {
        fun create(query: String): SearchPagingSource {
            return SearchPagingSource(query, service)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RelatedClaim> {
        val page = params.key ?: 1
        val relatedClaims = service.search(
            query = query,
            resolve = true,
            pageSize = params.loadSize,
            page = page,
        )
        val endOfPaginationReached = relatedClaims.isEmpty()
        return LoadResult.Page(
            data = relatedClaims,
            prevKey = null,
            nextKey = if (endOfPaginationReached) null else page.inc()
        )
    }

    override fun getRefreshKey(state: PagingState<Int, RelatedClaim>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
