package app.newproj.lbrytv.data.dto

import com.google.gson.annotations.SerializedName

data class SyncSetResult(
    @SerializedName("changed")
    val changed: Boolean?,

    @SerializedName("hash")
    val hash: String?,
)
