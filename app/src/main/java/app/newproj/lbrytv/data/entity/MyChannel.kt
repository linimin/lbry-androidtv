package app.newproj.lbrytv.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_channel")
data class MyChannel(
    @PrimaryKey val id: String,
)
