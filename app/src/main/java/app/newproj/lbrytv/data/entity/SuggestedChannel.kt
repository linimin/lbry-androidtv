package app.newproj.lbrytv.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suggested_claim")
data class SuggestedChannel(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
)
