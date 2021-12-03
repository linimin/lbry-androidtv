package app.newproj.lbrytv.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.newproj.lbrytv.data.entity.TrendingClaim

@Dao
abstract class TrendingClaimDao : BaseDao<TrendingClaim>() {
    @Query("DELETE from trending_claim")
    abstract suspend fun clear()
}
