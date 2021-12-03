package app.newproj.lbrytv.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.newproj.lbrytv.data.dao.*
import app.newproj.lbrytv.data.entity.*
import app.newproj.lbrytv.data.typeconverter.InstantTypeConverter
import app.newproj.lbrytv.data.typeconverter.UriTypeConverter

@Database(
    entities = [
        Claim::class,
        RelatedClaim::class,
        RemoteKey::class,
        Setting::class,
        SubscribedContent::class,
        Subscription::class,
        SuggestedChannel::class,
        TrendingClaim::class,
        User::class,
        MyChannel::class,
    ], version = 1
)
@TypeConverters(
    value = [
        InstantTypeConverter::class,
        UriTypeConverter::class,
    ]
)
abstract class MainDatabase : RoomDatabase() {
    abstract fun claimDao(): ClaimDao
    abstract fun relatedClaimDao(): RelatedClaimDao
    abstract fun remoteKeyDao(): RemoteKeyDao
    abstract fun settingDao(): SettingDao
    abstract fun subscribedContentDao(): SubscribedContentDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun suggestedChannelDao(): SuggestedChannelDao
    abstract fun trendingClaimDao(): TrendingClaimDao
    abstract fun userDao(): UserDao
    abstract fun myChannelDao(): MyChannelDao
}
