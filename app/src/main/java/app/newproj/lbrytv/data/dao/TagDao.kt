package app.newproj.lbrytv.data.dao

import androidx.room.Dao
import androidx.room.Query
import app.newproj.lbrytv.data.entity.Tag

@Dao
abstract class TagDao : BaseDao<Tag>() {
    @Query("SELECT * FROM tag")
    abstract fun tags(): List<Tag>

    @Query("DELETE FROM tag")
    abstract fun clear()
}
