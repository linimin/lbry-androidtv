package app.newproj.lbrytv.data.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import app.newproj.lbrytv.data.dto.ClaimType
import java.time.Instant

/**
 * A stake that contains metadata about a stream or channel.
 */
@Entity(tableName = "claim")
data class Claim(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "thumbnail")
    val thumbnail: Uri?,

    @ColumnInfo(name = "title")
    val title: String?,

    @ColumnInfo(name = "effective_amount")
    val effectiveAmount: String?,

    @ColumnInfo(name = "trending_group")
    val trendingGroup: Int?,

    @ColumnInfo(name = "trending_mixed")
    val trendingMixed: Double?,

    @ColumnInfo(name = "release_time")
    val releaseTime: Instant?,

    @ColumnInfo(name = "channel_name")
    val channelName: String?,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "value_type")
    val valueType: ClaimType?,

    @ColumnInfo(name = "permanent_url")
    val permanentUrl: String? = null,

    @ColumnInfo(name = "short_url")
    val shortUrl: String? = null,

    /**
     * A UTF-8 string of up to 255 bytes used to address the claim.
     */
    @ColumnInfo(name = "name")
    val name: String? = null,

    @ColumnInfo(name = "cover")
    val cover: Uri? = null,

    @ColumnInfo(name = "channel_id")
    val channelId: String? = null,
)
