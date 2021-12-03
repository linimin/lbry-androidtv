package app.newproj.lbrytv.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import app.newproj.lbrytv.data.entity.Setting

@Dao
abstract class SettingDao : BaseDao<Setting>() {
    @Query("SELECT * from setting")
    abstract fun settings(): PagingSource<Int, Setting>

    @Query("DELETE from setting")
    abstract suspend fun clear()
}
