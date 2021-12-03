package app.newproj.lbrytv.data.dto

import com.google.gson.annotations.SerializedName

data class ApplyWalletSyncRequest(
    /**
     * password to decrypt incoming and encrypt outgoing data
     */
    @SerializedName("password")
    val password: String? = null,

    /**
     * incoming sync data, if any
     */
    @SerializedName("data")
    val data: String? = null,

    /**
     * wallet being sync'ed
     */
    @SerializedName("wallet_id")
    val walletId: String? = null,

    /**
     * wait until any new accounts have sync'ed
     */
    @SerializedName("blocking")
    val blocking: Boolean? = null,
)
