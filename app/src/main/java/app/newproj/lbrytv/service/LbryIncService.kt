package app.newproj.lbrytv.service

import app.newproj.lbrytv.data.dto.*
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * The implementation of the service interface is provided by
 * [app.newproj.lbrytv.hiltmodule.LbryIncServiceModule]
 */
interface LbryIncService {
    @GET("/user/me")
    suspend fun tokenUser(): TokenUser

    @POST("/user/new")
    suspend fun newUser(
        @Query("language") language: String,
        @Query("app_id") appId: String
    ): NewUser

    @POST("/user/signout")
    suspend fun signOut()

    @GET("/subscription/list")
    suspend fun subscriptions(): List<Subscription>

    @POST("/user_email/new")
    suspend fun addEmail(
        @Query("email") email: String,
        @Query("send_verification_email") sendVerificationEmail: Boolean,
    ): String

    @POST("/user_email/resend_token")
    suspend fun verifyEmail(
        @Query("email") email: String,
        @Query("only_if_expired") onlyIfExpired: Boolean,
    ): String

    @GET("/subscription/new")
    suspend fun subscribeChannel(
        @Query("claim_id") claimId: String,
        @Query("channel_name") channelName: String,
        @Query("notifications_disabled") notificationsDisabled: Boolean,
    ): Subscription

    @GET("/subscription/delete")
    suspend fun unsubscribeChannel(
        @Query("claim_id") claimId: String,
    )

    @POST("/sync/get")
    suspend fun walletSyncData(@Query("hash") hash: String): WalletSyncData

    @POST("/sync/set")
    suspend fun setWalletSyncData(
        @Query("old_hash") oldHash: String?,
        @Query("new_hash") newHash: String?,
        @Query("data") data: String?,
    ): SyncSetResult
}
