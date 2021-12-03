package app.newproj.lbrytv.data.dao

import androidx.room.Dao
import androidx.room.Query
import app.newproj.lbrytv.data.entity.SubscribedContent

@Dao
abstract class SubscribedContentDao: BaseDao<SubscribedContent>() {
    @Query("DELETE from subscribed_content")
    abstract suspend fun clear()
}
