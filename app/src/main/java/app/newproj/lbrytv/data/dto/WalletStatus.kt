package app.newproj.lbrytv.data.dto

import com.google.gson.annotations.SerializedName

data class WalletStatus(
    @SerializedName("is_encrypted")
    val isEncrypted: Boolean? = null,

    @SerializedName("is_locked")
    val isLocked: Boolean? = null,

    @SerializedName("is_syncing")
    val isSyncing: Boolean? = null,
)
