package app.newproj.lbrytv.data.dto

import com.google.gson.annotations.SerializedName

data class EmailVerificationRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("send_verification_email")
    val sendVerificationEmail: Boolean,
)
