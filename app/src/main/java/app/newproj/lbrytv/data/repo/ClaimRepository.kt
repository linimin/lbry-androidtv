package app.newproj.lbrytv.data.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import app.newproj.lbrytv.data.MainDatabase
import app.newproj.lbrytv.data.entity.Claim
import app.newproj.lbrytv.service.LbryIncService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

private const val PAGE_SIZE = 100

@OptIn(ExperimentalPagingApi::class)
class ClaimRepository @Inject constructor(
    private val db: MainDatabase,
    private val trendingRemoteMediator: TrendingRemoteMediator,
    private val suggestedChannelRemoteMediator: SuggestedChannelRemoteMediator,
    private val searchRemoteMediatorFactory: SearchRemoteMediator.Factory,
    private val subscriptionRemoteMediator: SubscriptionRemoteMediator,
    private val subscribedContentRemoteMediatorFactory: SubscribedContentRemoteMediator.Factory,
    private val channelRemoteMediatorFactory: ChannelRemoteMediator.Factory,
    private val myChannelRemoteMediator: MyChannelRemoteMediator,
    private val lbryIncService: LbryIncService,
) {
    fun claim(id: String): Flow<Claim> {
        return db.claimDao().claimById(id)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun subscribedContent(): Flow<PagingData<Claim>> = db.subscriptionDao()
        .subscriptionsFlow()
        .flatMapLatest {
            Pager(
                config = PagingConfig(pageSize = PAGE_SIZE),
                remoteMediator = subscribedContentRemoteMediatorFactory.create(it),
                pagingSourceFactory = { db.claimDao().subscribedContent() }
            ).flow
        }

    fun trending(): Flow<PagingData<Claim>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        remoteMediator = trendingRemoteMediator,
        pagingSourceFactory = { db.claimDao().trending() }
    ).flow

    fun subscription(): Flow<PagingData<Claim>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        remoteMediator = subscriptionRemoteMediator,
        pagingSourceFactory = { db.claimDao().subscriptions() }
    ).flow

    fun suggestedChannels(): Flow<PagingData<Claim>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        remoteMediator = suggestedChannelRemoteMediator,
        pagingSourceFactory = { db.claimDao().suggestedChannels() }
    ).flow

    fun search(query: String): Flow<PagingData<Claim>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        remoteMediator = searchRemoteMediatorFactory.create(query),
        pagingSourceFactory = { db.claimDao().related() }
    ).flow

    fun claimsByChannelId(channelId: String): Flow<PagingData<Claim>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        remoteMediator = channelRemoteMediatorFactory.create(channelId),
        pagingSourceFactory = { db.claimDao().claimsByChannelId(channelId) }
    ).flow

    fun myChannels(): Flow<PagingData<Claim>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        remoteMediator = myChannelRemoteMediator,
        pagingSourceFactory = { db.claimDao().myChannels() }
    ).flow

    suspend fun deleteUserRelatedClaims() {
        db.subscriptionDao().clear()
        db.subscribedContentDao().clear()
    }

    suspend fun addSubscription(claim: Claim) {
        val subscription = lbryIncService.subscribeChannel(claim.id, claim.name!!, false)
        db.withTransaction {
            db.subscriptionDao().insertSubscriptions(listOf(subscription))
        }
    }

    suspend fun removeSubscription(claim: Claim) {
        lbryIncService.unsubscribeChannel(claim.id)
        db.withTransaction {
            db.subscriptionDao().deleteByClaimId(claim.id)
        }
    }
}
