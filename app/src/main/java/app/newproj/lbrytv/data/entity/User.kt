package app.newproj.lbrytv.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "auth_token")
    val authToken: String?,

    @ColumnInfo(name = "has_verified_email")
    val hasVerifiedEmail: Boolean?,
)
