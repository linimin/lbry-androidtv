package app.newproj.lbrytv.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscription")
data class Subscription(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "channel_name")
    val channelName: String?,

    @ColumnInfo(name = "claim_id")
    val claimId: String?,
)
