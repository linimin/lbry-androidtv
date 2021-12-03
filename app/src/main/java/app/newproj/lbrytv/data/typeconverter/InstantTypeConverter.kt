package app.newproj.lbrytv.data.typeconverter

import androidx.room.TypeConverter
import java.time.Instant

object InstantTypeConverter {
    @TypeConverter
    fun fromTimeStamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it * 1000) }
    }

    @TypeConverter
    fun instantToMilli(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }
}
