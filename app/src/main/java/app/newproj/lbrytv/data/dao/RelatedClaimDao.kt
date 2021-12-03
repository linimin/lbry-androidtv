package app.newproj.lbrytv.data.dao

import androidx.room.Dao
import androidx.room.Query
import app.newproj.lbrytv.data.entity.RelatedClaim

@Dao
abstract class RelatedClaimDao: BaseDao<RelatedClaim>() {
    @Query("DELETE from related_claim")
    abstract suspend fun clear()
}
