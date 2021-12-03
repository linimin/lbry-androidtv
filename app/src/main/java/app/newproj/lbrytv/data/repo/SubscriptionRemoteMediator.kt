package app.newproj.lbrytv.data.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import app.newproj.lbrytv.util.LbryUri
import app.newproj.lbrytv.data.MainDatabase
import app.newproj.lbrytv.data.dto.ClaimResolveRequest
import app.newproj.lbrytv.data.entity.Claim
import app.newproj.lbrytv.hiltmodule.LbrynetServiceProxyScope
import app.newproj.lbrytv.service.NullDataResponseException
import app.newproj.lbrytv.service.LbryIncService
import app.newproj.lbrytv.service.LbrynetService
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class SubscriptionRemoteMediator @Inject constructor(
    private val lbryIncService: LbryIncService,
    @LbrynetServiceProxyScope private val lbrynetService: LbrynetService,
    private val db: MainDatabase,
) : RemoteMediator<Int, Claim>() {

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, Claim>
    ): MediatorResult {
        return try {
            if (loadType == LoadType.REFRESH) {
                val subscriptions = lbryIncService.subscriptions()
                val lbryUrls = subscriptions.map {
                    LbryUri.Builder()
                        .setClaimId(it.claimId!!)
                        .setChannelName(it.channelName!!)
                        .build()
                        .toString()
                }
                val request = ClaimResolveRequest(lbryUrls)
                val resolvedClaims = lbrynetService.resolveClaims(request).values.toList()
                db.withTransaction {
                    db.subscriptionDao().clear()
                    db.subscriptionDao().insertOrUpdateSubscriptions(subscriptions)
                    db.claimDao().insertOrUpdateResolved(resolvedClaims)
                }
            }
            MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: NullDataResponseException) {
            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: NullDataResponseException) {
            MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}
