package app.newproj.lbrytv.data.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import app.newproj.lbrytv.data.MainDatabase
import app.newproj.lbrytv.data.dto.ClaimSearchRequest
import app.newproj.lbrytv.data.entity.*
import app.newproj.lbrytv.hiltmodule.LbrynetServiceProxyScope
import app.newproj.lbrytv.service.LbrynetService
import app.newproj.lbrytv.service.NullDataResponseException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val STARTING_PAGE_INDEX = 1
private const val REMOTE_KEY_LABEL = "subscribed_content"

@OptIn(ExperimentalPagingApi::class)
class SubscribedContentRemoteMediator(
    private val subscriptions: List<Subscription>,
    @LbrynetServiceProxyScope private val service: LbrynetService,
    private val db: MainDatabase,
) : RemoteMediator<Int, Claim>() {

    class Factory @Inject constructor(
        @LbrynetServiceProxyScope private val service: LbrynetService,
        private val db: MainDatabase,
    ) {
        fun create(subscriptions: List<Subscription>): SubscribedContentRemoteMediator {
            return SubscribedContentRemoteMediator(subscriptions, service, db)
        }
    }

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
            val channelIds = subscriptions.mapNotNull { it.claimId }
            if (channelIds.isEmpty()) return MediatorResult.Success(endOfPaginationReached = true)
            val searchRequest = ClaimSearchRequest(
                claimType = listOf("stream", "repost"),
                channelIds = channelIds,
                orderBy = listOf("release_time"),
                page = page,
                hasSource = true,
                pageSize = state.config.pageSize,
            )
            val claimSearchResult = service.searchClaims(searchRequest)
            val claims = claimSearchResult.items?.filterNotNull() ?: emptyList()
            val endOfPaginationReached = claims.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeyDao().deleteByLabel(REMOTE_KEY_LABEL)
                    db.subscribedContentDao().clear()
                }
                db.claimDao().insertOrUpdateSearchResult(claims)
                db.subscribedContentDao().insertOrUpdate(claims.map { SubscribedContent(it.claimId) })
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
}
