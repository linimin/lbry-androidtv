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

import app.newproj.lbrytv.data.dto.LbryIncServiceResponse
import app.newproj.lbrytv.data.dto.NewUser
import app.newproj.lbrytv.data.dto.SignInResponse
import app.newproj.lbrytv.data.dto.Subscription
import app.newproj.lbrytv.data.dto.SyncSetResult
import app.newproj.lbrytv.data.dto.TokenUser
import app.newproj.lbrytv.data.dto.WalletSyncData
import app.newproj.lbrytv.data.repo.AuthTokenRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import javax.inject.Inject

interface LbryIncService {
    @POST("/user/signin")
    suspend fun signIn(
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("auth_token") authToken: String? = null,
    ): SignInResponse

    @POST("/user/signout")
    suspend fun signOut()

    @POST("/user/new")
    suspend fun newUser(
        @Query("language") language: String,
        @Query("app_id") appId: String,
    ): NewUser

    @GET("/user/me")
    suspend fun tokenUser(@Query("auth_token") authToken: String? = null): TokenUser

    @DELETE("/user/signout")
    suspend fun revokeToken()

    @POST("/user_email/new")
    suspend fun registerEmail(
        @Query("email") email: String,
        @Query("send_verification_email") sendVerificationEmail: Boolean,
        @Query("auth_token") authToken: String? = null,
    ): String

    @POST("/user_email/resend_token")
    suspend fun verifyEmail(
        @Query("email") email: String,
        @Query("only_if_expired") onlyIfExpired: Boolean,
        @Query("auth_token") authToken: String? = null,
    ): String

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

    @GET("/subscription/sub_count")
    suspend fun subscriptionCount(@Query("claim_id") claimId: String): List<Int>

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

private const val KEY_AUTH_TOKEN = "auth_token"

class LbryIncServiceAuthInterceptor @Inject constructor(
    private val authTokenRepo: AuthTokenRepository,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url
        if (url.queryParameter(KEY_AUTH_TOKEN) != null || url.encodedPath == "/user/new") {
            return chain.proceed(request)
        } else {
            val authToken = runBlocking { authTokenRepo.authToken() }
            return if (authToken != null) {
                val newUrl = url.newBuilder()
                    .addQueryParameter(KEY_AUTH_TOKEN, authToken)
                    .build()
                val newRequest = request.newBuilder()
                    .url(newUrl)
                    .build()
                chain.proceed(newRequest)
            } else {
                chain.proceed(request)
            }
        }
    }
}

class LbryIncServiceErrorInterceptor @Inject constructor(private val gson: Gson) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return if (response.isSuccessful) {
            response
        } else {
            val errorMessage = gson.fromJson<LbryIncServiceResponse<Any>>(
                response.body?.source()
                    ?.apply { request(Long.MAX_VALUE) }
                    ?.buffer?.clone()
                    ?.readString(StandardCharsets.UTF_8),
                TypeToken.getParameterized(LbryIncServiceResponse::class.java, Any::class.java).type
            ).error ?: response.message
            response.newBuilder()
                .message(errorMessage)
                .build()
        }
    }
}

object LbryIncServiceBodyConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): Converter<ResponseBody, *> {
        val delegate = retrofit.nextResponseBodyConverter<LbryIncServiceResponse<*>>(
            this,
            TypeToken.getParameterized(LbryIncServiceResponse::class.java, type).type,
            annotations
        )
        return ResponseBodyConverter(delegate)
    }

    private class ResponseBodyConverter<T>(
        private val delegate: Converter<ResponseBody, LbryIncServiceResponse<out T>>,
    ) : Converter<ResponseBody, T> {
        override fun convert(value: ResponseBody): T? {
            return delegate.convert(value)?.data
        }
    }
}
