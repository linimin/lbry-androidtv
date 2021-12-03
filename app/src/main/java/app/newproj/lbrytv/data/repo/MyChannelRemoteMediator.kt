package app.newproj.lbrytv.data.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import app.newproj.lbrytv.data.MainDatabase
import app.newproj.lbrytv.data.dto.ClaimType
import app.newproj.lbrytv.data.dto.MyClaimSearchRequest
import app.newproj.lbrytv.data.entity.Claim
import app.newproj.lbrytv.data.entity.MyChannel
import app.newproj.lbrytv.data.entity.RemoteKey
import app.newproj.lbrytv.hiltmodule.LbrynetServiceScope
import app.newproj.lbrytv.service.LbrynetService
import app.newproj.lbrytv.service.NullDataResponseException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val STARTING_PAGE_INDEX = 1
private const val REMOTE_KEY_LABEL = "my_channels"

@OptIn(ExperimentalPagingApi::class)
class MyChannelRemoteMediator @Inject constructor(
    @LbrynetServiceScope private val service: LbrynetService,
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
            val request = MyClaimSearchRequest(
                claimTypes = listOf(ClaimType.CHANNEL_CLAIM),
                page = page,
                pageSize = state.config.pageSize,
            )
            val result = service.myClaims(request)
            val claims = result.items?.filterNotNull() ?: emptyList()
            val endOfPaginationReached = claims.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeyDao().deleteByLabel(REMOTE_KEY_LABEL)
                    db.trendingClaimDao().clear()
                }
                db.claimDao().insertOrUpdateMyChannel(claims)
                db.myChannelDao().insertOrUpdate(claims.map { MyChannel(it.claimId) })
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
