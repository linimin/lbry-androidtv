package app.newproj.lbrytv.data.dao

import androidx.room.Dao
import androidx.room.Query
import app.newproj.lbrytv.data.entity.RemoteKey

@Dao
abstract class RemoteKeyDao : BaseDao<RemoteKey>() {
    @Query("SELECT * FROM remote_key WHERE label = :label")
    abstract suspend fun findByLabel(label: String): RemoteKey?

    @Query("DELETE FROM remote_key WHERE label = :label")
    abstract suspend fun deleteByLabel(label: String)

    @Query("DELETE from remote_key")
    abstract suspend fun clear()
}
