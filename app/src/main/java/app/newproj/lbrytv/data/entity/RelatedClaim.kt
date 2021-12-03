package app.newproj.lbrytv.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "related_claim")
data class RelatedClaim(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
)
