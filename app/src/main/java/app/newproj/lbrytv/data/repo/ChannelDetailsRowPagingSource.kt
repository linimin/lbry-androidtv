package app.newproj.lbrytv.data.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.ChannelDetailsOverview
import app.newproj.lbrytv.data.dto.ClaimCard
import app.newproj.lbrytv.data.dto.PagingDataList
import app.newproj.lbrytv.data.dto.RowPresentable
import app.newproj.lbrytv.data.entity.Claim
import app.newproj.lbrytv.util.cast
import app.newproj.lbrytv.util.mapPagingData
import java.io.IOException
import javax.inject.Inject

class ChannelDetailsRowPagingSource private constructor(
    private val claim: Claim,
    private val claimRepo: ClaimRepository,
    private val isFollowing: Boolean,
) : PagingSource<Int, RowPresentable>() {

    class Factory @Inject constructor(private val claimRepository: ClaimRepository) {
        fun create(claim: Claim, isFollowing: Boolean): ChannelDetailsRowPagingSource {
            return ChannelDetailsRowPagingSource(claim, claimRepository, isFollowing)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RowPresentable> {
        return try {
            val latestVideos = claimRepo.claimsByChannelId(claim.id).mapPagingData { ClaimCard(it) }
            var id = -1L
            LoadResult.Page(
                listOf(
                    ChannelDetailsOverview(++id, claim, isFollowing),
                    PagingDataList(++id, R.string.latest_videos, null, latestVideos.cast()),
                ), null, null
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RowPresentable>): Int? = null
}
