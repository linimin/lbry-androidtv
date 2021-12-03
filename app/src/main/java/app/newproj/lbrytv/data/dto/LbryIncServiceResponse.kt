package app.newproj.lbrytv.data.dto

import com.google.gson.annotations.SerializedName

data class LbryIncServiceResponse<T>(
    @SerializedName("success")
    val success: Boolean? = null,

    @SerializedName("error")
    val error: Any? = null,

    @SerializedName("data")
    val data: T? = null,
)
