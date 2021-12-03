package app.newproj.lbrytv.data.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import app.newproj.lbrytv.data.MainDatabase
import app.newproj.lbrytv.data.entity.Claim
import app.newproj.lbrytv.data.entity.RelatedClaim
import app.newproj.lbrytv.data.entity.RemoteKey
import app.newproj.lbrytv.service.LighthouseService
import app.newproj.lbrytv.service.NullDataResponseException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val STARTING_PAGE_INDEX = 0
private const val REMOTE_KEY_LABEL = "search"

@OptIn(ExperimentalPagingApi::class)
class SearchRemoteMediator(
    private val query: String,
    private val service: LighthouseService,
    private val db: MainDatabase,
) : RemoteMediator<Int, Claim>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Claim>): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> STARTING_PAGE_INDEX
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = db.remoteKeyDao().findByLabel(REMOTE_KEY_LABEL)
                    remoteKey?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }
            val relatedClaims = service.search(
                query = query,
                pageSize = state.config.pageSize,
                page = page,
            )
            val endOfPaginationReached = relatedClaims.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeyDao().deleteByLabel(REMOTE_KEY_LABEL)
                    db.relatedClaimDao().clear()
                }
                db.claimDao().insertOrUpdateRelated(relatedClaims)
                db.relatedClaimDao().insertOrUpdate(relatedClaims.map { RelatedClaim(it.id) })
                val nextKey = if (endOfPaginationReached) null else page.inc()
                val remoteKey = RemoteKey(REMOTE_KEY_LABEL, nextKey)
                db.remoteKeyDao().insertOrUpdate(remoteKey)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: NullDataResponseException) {
            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    class Factory @Inject constructor(
        private val lighthouseService: LighthouseService,
        private val db: MainDatabase,
    ) {
        fun create(query: String): SearchRemoteMediator {
            return SearchRemoteMediator(query, lighthouseService, db)
        }
    }
}
