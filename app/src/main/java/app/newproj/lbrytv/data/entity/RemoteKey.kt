package app.newproj.lbrytv.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_key")
data class RemoteKey(
    @PrimaryKey
    @ColumnInfo(name = "label")
    val label: String,

    @ColumnInfo(name = "next_key")
    val nextKey: Int?,
)
