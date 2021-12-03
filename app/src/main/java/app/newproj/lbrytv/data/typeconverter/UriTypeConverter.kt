package app.newproj.lbrytv.data.typeconverter

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter

object UriTypeConverter {
    @TypeConverter
    fun fromUriString(value: String?): Uri? {
        return value?.toUri()
    }

    @TypeConverter
    fun uriToString(url: Uri?): String? {
        return url?.toString()
    }
}
