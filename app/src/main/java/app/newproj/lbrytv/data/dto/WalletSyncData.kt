package app.newproj.lbrytv.data.dto

import com.google.gson.annotations.SerializedName

data class WalletSyncData(
    @SerializedName("changed")
    val changed: Boolean? = null,

    @SerializedName("data")
    val data: String? = null,

    @SerializedName("hash")
    val hash: String? = null,

    @SerializedName("last_updated")
    val lastUpdated: Int? = null,
)
