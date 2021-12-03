package app.newproj.lbrytv.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.newproj.lbrytv.data.dto.RowPresentable
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Provider

private const val PAGE_SIZE = 100

class HomeRowRepository @Inject constructor(
    private val homeRowPagingSourceProvider: Provider<HomeRowPagingSource>,
) {
    fun rowModels(): Flow<PagingData<RowPresentable>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = { homeRowPagingSourceProvider.get() }
    ).flow
}
