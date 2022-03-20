/*
 * MIT License
 *
 * Copyright (c) 2022 LIN I MIN
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package app.newproj.lbrytv.data

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.Database
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import app.newproj.lbrytv.data.dao.BrowseCategoryDao
import app.newproj.lbrytv.data.dao.ClaimDao
import app.newproj.lbrytv.data.dao.ClaimLookupDao
import app.newproj.lbrytv.data.dao.ClaimSearchResultDao
import app.newproj.lbrytv.data.dao.FollowingChannelDao
import app.newproj.lbrytv.data.dao.RelatedClaimDao
import app.newproj.lbrytv.data.dao.RemoteKeyDao
import app.newproj.lbrytv.data.dao.ResolvedClaimDao
import app.newproj.lbrytv.data.dao.SettingDao
import app.newproj.lbrytv.data.entity.BrowseCategory
import app.newproj.lbrytv.data.entity.Claim
import app.newproj.lbrytv.data.entity.ClaimLookup
import app.newproj.lbrytv.data.entity.FollowingChannel
import app.newproj.lbrytv.data.entity.RemoteKey
import app.newproj.lbrytv.data.entity.Setting
import java.math.BigDecimal
import java.time.Instant

@Database(
    entities = [
        BrowseCategory::class,
        Claim::class,
        ClaimLookup::class,
        FollowingChannel::class,
        RemoteKey::class,
        Setting::class,
    ],
    version = 1
)
@TypeConverters(
    value = [
        BigDecimalTypeConverter::class,
        InstantTypeConverter::class,
        UriTypeConverter::class,
    ]
)
@RewriteQueriesToDropUnusedColumns
abstract class AppDatabase : RoomDatabase() {
    abstract fun browseCategoryDao(): BrowseCategoryDao
    abstract fun claimDao(): ClaimDao
    abstract fun claimLookupDao(): ClaimLookupDao
    abstract fun claimSearchResultDao(): ClaimSearchResultDao
    abstract fun followingChannelDao(): FollowingChannelDao
    abstract fun relatedClaimDao(): RelatedClaimDao
    abstract fun remoteKeyDao(): RemoteKeyDao
    abstract fun resolvedClaimDao(): ResolvedClaimDao
    abstract fun settingDao(): SettingDao
}

object BigDecimalTypeConverter {
    @TypeConverter
    fun bigDecimalFromString(value: String?): BigDecimal? {
        return value?.toBigDecimalOrNull()
    }

    @TypeConverter
    fun bigDecimalToString(value: BigDecimal?): String? {
        return value?.toPlainString()
    }
}

object InstantTypeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it * 1000) }
    }

    @TypeConverter
    fun instantToTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }
}

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
