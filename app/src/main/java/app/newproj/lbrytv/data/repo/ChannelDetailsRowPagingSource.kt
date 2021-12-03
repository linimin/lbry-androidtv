package app.newproj.lbrytv.data.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.withTransaction
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.MainDatabase
import app.newproj.lbrytv.data.dto.ChannelDetailsOverview
import app.newproj.lbrytv.data.dto.ClaimCard
import app.newproj.lbrytv.data.dto.PagingDataList
import app.newproj.lbrytv.data.dto.RowPresentable
import app.newproj.lbrytv.data.entity.Claim
import app.newproj.lbrytv.util.cast
import app.newproj.lbrytv.util.mapPagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class ChannelDetailsRowPagingSource private constructor(
    private val claim: Claim,
    private val claimRepo: ClaimRepository,
    private val db: MainDatabase,
    pagingFlowJob: Job,
) : PagingSource<Int, RowPresentable>() {

    class Factory @Inject constructor(
        private val claimRepository: ClaimRepository,
        private val db: MainDatabase,
    ) {
        fun create(claim: Claim, pagingFlowJob: Job): ChannelDetailsRowPagingSource {
            return ChannelDetailsRowPagingSource(claim, claimRepository, db, pagingFlowJob)
        }
    }

    private val scope = CoroutineScope(pagingFlowJob)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RowPresentable> {
        try {
            val isFollowing = db.withTransaction {
                db.subscriptionDao().subscriptionById(claim.id) != null
            }
            scope.launch {
                db.subscriptionDao()
                    .subscriptionFlowById(claim.id)
                    .collect {
                        if ((it != null) != isFollowing) {
                            invalidate()
                            cancel()
                        }
                    }
            }
            val latestVideos = claimRepo.claimsByChannelId(claim.id).mapPagingData { ClaimCard(it) }
            var id = -1L
            return LoadResult.Page(
                listOf(
                    ChannelDetailsOverview(++id, claim, isFollowing),
                    PagingDataList(++id, R.string.latest_videos, latestVideos.cast()),
                ), null, null
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RowPresentable>): Int? = null
}
