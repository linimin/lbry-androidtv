package app.newproj.lbrytv.data.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.MainDatabase
import app.newproj.lbrytv.data.dto.SettingType
import app.newproj.lbrytv.data.entity.Setting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val PAGE_SIZE = 100

@OptIn(ExperimentalPagingApi::class)
class SettingRepository @Inject constructor(
    private val db: MainDatabase,
    private val settingRemoteMediator: SettingRemoteMediator,
) {

    fun settings(): Flow<PagingData<Setting>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        remoteMediator = settingRemoteMediator,
        pagingSourceFactory = { db.settingDao().settings() }
    ).flow
}
