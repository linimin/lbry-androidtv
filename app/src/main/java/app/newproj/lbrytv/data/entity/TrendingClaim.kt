package app.newproj.lbrytv.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trending_claim")
data class TrendingClaim(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
)
