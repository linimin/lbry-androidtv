package app.newproj.lbrytv.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscribed_content")
data class SubscribedContent(
    @PrimaryKey val id: String,
)
