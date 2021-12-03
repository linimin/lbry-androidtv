package app.newproj.lbrytv.data.dto

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.google.gson.annotations.SerializedName

data class WalletBalance(
    @SerializedName("available")
    @ColumnInfo(name = "available")
    val available: String? = null,

    @SerializedName("reserved")
    @ColumnInfo(name = "reserved")
    val reserved: String? = null,

    @SerializedName("reserved_subtotals")
    @Embedded
    val reservedSubtotals: ReservedSubtotals? = null,

    @SerializedName("total")
    @ColumnInfo(name = "total")
    val total: String? = null
) {
    data class ReservedSubtotals(
        @SerializedName("claims")
        @ColumnInfo(name = "claims")
        val claims: String? = null,

        @SerializedName("supports")
        @ColumnInfo(name = "supports")
        val supports: String? = null,

        @SerializedName("tips")
        @ColumnInfo(name = "tips")
        val tips: String? = null
    )
}
