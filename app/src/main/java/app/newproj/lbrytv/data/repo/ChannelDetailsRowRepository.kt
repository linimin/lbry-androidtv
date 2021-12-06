package app.newproj.lbrytv.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.newproj.lbrytv.data.MainDatabase
import app.newproj.lbrytv.data.dto.RowPresentable
import app.newproj.lbrytv.data.entity.Claim
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val PAGE_SIZE = 10

class ChannelDetailsRowRepository @Inject constructor(
    private val db: MainDatabase,
    private val rowPagingSourceFactory: ChannelDetailsRowPagingSource.Factory,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun rows(claim: Claim): Flow<PagingData<RowPresentable>> {
        return db.subscriptionDao()
            .subscriptionFlowById(claim.id)
            .map { it != null }
            .flatMapLatest { isFollowing ->
                Pager(
                    config = PagingConfig(pageSize = PAGE_SIZE),
                    pagingSourceFactory = {
                        rowPagingSourceFactory.create(claim, isFollowing)
                    }
                ).flow
            }
    }
}
