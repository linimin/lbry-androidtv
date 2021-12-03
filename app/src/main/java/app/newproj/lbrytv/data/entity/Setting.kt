package app.newproj.lbrytv.data.entity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import app.newproj.lbrytv.data.dto.SettingType

@Entity(tableName = "setting")
data class Setting(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "title")
    @StringRes
    val title: Int,

    @ColumnInfo(name = "type")
    val type: SettingType,

    @ColumnInfo(name = "icon")
    @DrawableRes
    val icon: Int,
)
