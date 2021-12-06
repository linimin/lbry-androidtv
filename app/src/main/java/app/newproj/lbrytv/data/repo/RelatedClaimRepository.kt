package app.newproj.lbrytv.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.newproj.lbrytv.data.dto.RelatedClaim
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val PAGE_SIZE = 25

class RelatedClaimRepository @Inject constructor(
    private val searchPagingSourceFactory: SearchPagingSource.Factory,
) {
    fun relatedClaims(query: String): Flow<PagingData<RelatedClaim>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = { searchPagingSourceFactory.create(query) }
    ).flow
}
