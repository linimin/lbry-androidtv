package app.newproj.lbrytv.data.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import app.newproj.lbrytv.data.MainDatabase
import app.newproj.lbrytv.data.dto.RelatedClaim
import app.newproj.lbrytv.service.LighthouseService
import com.bumptech.glide.load.HttpException
import java.io.IOException
import javax.inject.Inject

class SearchPagingSource private constructor(
    private val query: String,
    private val service: LighthouseService,
    private val db: MainDatabase,
) : PagingSource<Int, RelatedClaim>() {

    class Factory @Inject constructor(
        private val service: LighthouseService,
        private val db: MainDatabase,
    ) {
        fun create(query: String): SearchPagingSource {
            return SearchPagingSource(query, service, db)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RelatedClaim> {
        try {
            val page = params.key ?: 0
            val relatedClaims = service.search(
                query = query,
                resolve = true,
                pageSize = params.loadSize,
                page = page,
            )
            db.withTransaction {
                db.claimDao().insertOrUpdateRelated(relatedClaims)
            }
            val endOfPaginationReached = relatedClaims.isEmpty()
            return LoadResult.Page(
                data = relatedClaims,
                prevKey = null,
                nextKey = if (endOfPaginationReached) null else page.inc()
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RelatedClaim>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
