package app.newproj.lbrytv.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.newproj.lbrytv.data.entity.Setting
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Provider

private const val PAGE_SIZE = 100

class SettingRepository @Inject constructor(
    private val settingPagingSourceProvider: Provider<SettingPagingSource>,
) {

    fun settings(): Flow<PagingData<Setting>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = { settingPagingSourceProvider.get() }
    ).flow
}
