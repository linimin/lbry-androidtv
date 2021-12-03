package app.newproj.lbrytv.data.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import app.newproj.lbrytv.data.MainDatabase
import app.newproj.lbrytv.data.dto.ClaimSearchRequest
import app.newproj.lbrytv.data.entity.Claim
import app.newproj.lbrytv.data.entity.RemoteKey
import app.newproj.lbrytv.data.entity.SuggestedChannel
import app.newproj.lbrytv.hiltmodule.LbrynetServiceProxyScope
import app.newproj.lbrytv.service.LbrynetService
import app.newproj.lbrytv.service.NullDataResponseException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val STARTING_PAGE_INDEX = 1
private const val REMOTE_KEY_LABEL = "suggested_channels"

@OptIn(ExperimentalPagingApi::class)
class SuggestedChannelRemoteMediator @Inject constructor(
    @LbrynetServiceProxyScope private val service: LbrynetService,
    private val db: MainDatabase,
) : RemoteMediator<Int, Claim>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Claim>): MediatorResult {
        try {
            val nextPageNumber = when (loadType) {
                LoadType.REFRESH -> STARTING_PAGE_INDEX
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = db.remoteKeyDao().findByLabel(REMOTE_KEY_LABEL)
                    remoteKey?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }
            val request = ClaimSearchRequest(
                claimType = listOf("channel"),
                orderBy = listOf("effective_amount"),
                hasSource = true,
                page = nextPageNumber,
                pageSize = state.config.pageSize,
            )
            val result = service.searchClaims(request)
            val claims = result.items?.filterNotNull() ?: emptyList()
            val endOfPaginationReached = claims.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeyDao().deleteByLabel(REMOTE_KEY_LABEL)
                    db.suggestedChannelDao().clear()
                }
                db.claimDao().insertOrUpdateSearchResult(claims)
                db.suggestedChannelDao().insertOrUpdate(claims.map { SuggestedChannel(it.claimId) })
                val nextKey = if (endOfPaginationReached) null else nextPageNumber.inc()
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
}
