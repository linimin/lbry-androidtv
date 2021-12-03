package app.newproj.lbrytv.data.dao

import androidx.room.Dao
import androidx.room.Query
import app.newproj.lbrytv.data.entity.SuggestedChannel

@Dao
abstract class SuggestedChannelDao : BaseDao<SuggestedChannel>() {
    @Query("DELETE FROM suggested_claim")
    abstract suspend fun clear()
}
