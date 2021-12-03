package app.newproj.lbrytv.data.dto

import com.google.gson.annotations.SerializedName

data class ApplyWalletSyncResult(
    @SerializedName("data")
    val data: String?,

    @SerializedName("hash")
    val hash: String?,
)
