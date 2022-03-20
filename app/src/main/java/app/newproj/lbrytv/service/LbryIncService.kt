/*
 * MIT License
 *
 * Copyright (c) 2022 LIN I MIN
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package app.newproj.lbrytv.service

import app.newproj.lbrytv.data.dto.NewUser
import app.newproj.lbrytv.data.dto.Subscription
import app.newproj.lbrytv.data.dto.SyncSetResult
import app.newproj.lbrytv.data.dto.TokenUser
import app.newproj.lbrytv.data.dto.WalletSyncData
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LbryIncService {
    /**
     * [Link to Postman](https://web.postman.co/workspace/LBRYtv~7307462d-f6f1-45d0-9455-7e36e7983a8c/request/19881064-a701b4f4-ef42-47c5-885f-994879a61921)
     */
    @POST("/user/new")
    suspend fun newUser(
        @Query("language") language: String,
        @Query("app_id") appId: String,
    ): NewUser

    /**
     * [Link to Postman](https://web.postman.co/workspace/LBRYtv~7307462d-f6f1-45d0-9455-7e36e7983a8c/request/19881064-e89a1d93-391d-4eea-9ca8-de74e0bb9abe)
     */
    @GET("/user/me")
    suspend fun tokenUser(@Query("auth_token") authToken: String? = null): TokenUser

    /**
     * [Link to Postman](https://web.postman.co/workspace/LBRYtv~7307462d-f6f1-45d0-9455-7e36e7983a8c/request/19881064-15cf9728-99d8-4ade-9f7f-86979b60ebd3)
     */
    @DELETE("/user/signout")
    suspend fun revokeToken()

    /**
     * [Link to Postman](https://web.postman.co/workspace/LBRYtv~7307462d-f6f1-45d0-9455-7e36e7983a8c/request/19881064-1bc66e24-b654-4e9b-bcb6-a410b1c79810)
     */
    @POST("/user_email/new")
    suspend fun registerEmail(
        @Query("email") email: String,
        @Query("send_verification_email") sendVerificationEmail: Boolean,
        @Query("auth_token") authToken: String? = null,
    ): String

    /**
     * [Link to Postman](https://web.postman.co/workspace/LBRYtv~7307462d-f6f1-45d0-9455-7e36e7983a8c/request/19881064-56b022f7-fa70-43c6-903d-9b13d53ca81e)
     */
    @POST("/user_email/resend_token")
    suspend fun verifyEmail(
        @Query("email") email: String,
        @Query("only_if_expired") onlyIfExpired: Boolean,
        @Query("auth_token") authToken: String? = null,
    ): String

    /**
     * [Link to Postman](https://web.postman.co/workspace/LBRYtv~7307462d-f6f1-45d0-9455-7e36e7983a8c/request/19881064-6e8450f0-7dd2-49fd-a41a-510579232386)
     */
    @GET("/subscription/list")
    suspend fun subscriptions(): List<Subscription>

    @POST("/subscription/new")
    suspend fun subscribeChannel(
        @Query("claim_id") claimId: String,
        @Query("channel_name") channelName: String,
        @Query("notifications_disabled") notificationsDisabled: Boolean,
    ): Subscription

    @DELETE("/subscription/delete")
    suspend fun unsubscribeChannel(
        @Query("claim_id") claimId: String,
    )

    /**
     * [Link to Postman](https://web.postman.co/workspace/LBRYtv~7307462d-f6f1-45d0-9455-7e36e7983a8c/request/19881064-94b52458-8c13-4dce-a010-ef3b9c909ba4)
     */
    @GET("/subscription/sub_count")
    suspend fun subscriptionCount(@Query("claim_id") claimId: String): List<Int>

    /**
     * [Link to Postman](https://web.postman.co/workspace/LBRYtv~7307462d-f6f1-45d0-9455-7e36e7983a8c/request/19881064-6233f190-1035-4612-b3fa-82382e62d4f9)
     */
    @GET("/file/view_count")
    suspend fun fileViewCount(@Query("claim_id") claimId: String): List<Int>

    @POST("/sync/get")
    suspend fun walletSyncData(@Query("hash") hash: String): WalletSyncData

    @POST("/sync/set")
    suspend fun setWalletSyncData(
        @Query("old_hash") oldHash: String?,
        @Query("new_hash") newHash: String?,
        @Query("data") data: String?,
    ): SyncSetResult
}
