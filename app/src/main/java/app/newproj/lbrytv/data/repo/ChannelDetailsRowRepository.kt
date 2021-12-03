package app.newproj.lbrytv.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.CoroutinesRoom
import app.newproj.lbrytv.data.dto.RowPresentable
import app.newproj.lbrytv.data.entity.Claim
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

private const val PAGE_SIZE = 10

class ChannelDetailsRowRepository @Inject constructor(
    private val rowPagingSourceFactory: ChannelDetailsRowPagingSource.Factory,
) {
    fun rows(claim: Claim): Flow<PagingData<RowPresentable>> {
        val pagingFlowJob = SupervisorJob()
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = {
                pagingFlowJob.cancelChildren()
                rowPagingSourceFactory.create(claim, pagingFlowJob)
            }
        ).flow.onCompletion {
            pagingFlowJob.cancel()
        }
    }
}
