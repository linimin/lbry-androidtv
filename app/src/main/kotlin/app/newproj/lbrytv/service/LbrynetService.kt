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

import app.newproj.lbrytv.data.dto.AccountList
import app.newproj.lbrytv.data.dto.ApplyWalletSyncRequest
import app.newproj.lbrytv.data.dto.ApplyWalletSyncResult
import app.newproj.lbrytv.data.dto.ClaimResolveRequest
import app.newproj.lbrytv.data.dto.ClaimSearchRequest
import app.newproj.lbrytv.data.dto.ClaimSearchResult
import app.newproj.lbrytv.data.dto.DownloadRequest
import app.newproj.lbrytv.data.dto.DownloadResponse
import app.newproj.lbrytv.data.dto.JsonRpc
import app.newproj.lbrytv.data.dto.LbrynetDaemonStatus
import app.newproj.lbrytv.data.dto.LbrynetVersion
import app.newproj.lbrytv.data.dto.MyClaimSearchRequest
import app.newproj.lbrytv.data.dto.MyClaimSearchResult
import app.newproj.lbrytv.data.dto.ResolvedClaim
import app.newproj.lbrytv.data.dto.SupportCreateRequest
import app.newproj.lbrytv.data.dto.SupportCreateResponse
import app.newproj.lbrytv.data.dto.UserPreference
import app.newproj.lbrytv.data.dto.UserPreferenceUpdateRequest
import app.newproj.lbrytv.data.dto.WalletBalance
import app.newproj.lbrytv.data.dto.WalletCreateRequest
import app.newproj.lbrytv.data.dto.WalletList
import app.newproj.lbrytv.data.dto.WalletRemoveRequest
import app.newproj.lbrytv.data.dto.WalletStatus
import app.newproj.lbrytv.data.repo.AuthTokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject

/**
 * A JSON-RPC API to interact with the LBRY network.
 * API documentation: https://lbry.tech/api/sdk
 */
interface LbrynetService {
    /**
     * Get the claim that a URL refers to.
     *
     * @param[request] A [ClaimResolveRequest] includes the request parameters.
     * @return a map of resolved claims, keyed by url.
     */
    @JsonRpc("resolve")
    @POST("/")
    suspend fun resolveClaims(@Body request: ClaimResolveRequest): Map<String, ResolvedClaim>

    /**
     * Get daemon status.
     *
     * @return a [LbrynetDaemonStatus] includes the daemon details.
     */
    @JsonRpc("status")
    @POST("/")
    suspend fun daemonStatus(): LbrynetDaemonStatus

    /**
     * Search for stream and channel claims on the blockchain.
     * Arguments marked with "supports equality constraints" allow prepending the value with an
     * equality constraint such as '>', '>=', '<' and '<=' eg. height=">400000" would limit
     * results to only claims above 400k block height.
     *
     * @param[request] A [ClaimSearchRequest] includes the request parameters.
     * @return a [ClaimSearchResult] contains the search result.
     */
    @JsonRpc("claim_search")
    @POST("/")
    suspend fun searchClaims(@Body request: ClaimSearchRequest): ClaimSearchResult

    /**
     * Return the balance of a wallet.
     *
     * @return a [WalletBalance] includes amount of lbry credits in wallet.
     */
    @JsonRpc("wallet_balance")
    @POST("/")
    suspend fun walletBalance(): WalletBalance

    /**
     * List my stream and channel claims.
     *
     * @param[request] A [MyClaimSearchRequest] includes the request parameters.
     * @return a [MyClaimSearchResult] contains the search result.
     */
    @JsonRpc("claim_list")
    @POST("/")
    suspend fun myClaims(@Body request: MyClaimSearchRequest): MyClaimSearchResult

    /**
     * Status of wallet including encryption/lock state.
     *
     * @return a [WalletStatus]
     */
    @JsonRpc("wallet_status")
    @POST("/")
    suspend fun walletStatus(): WalletStatus

    /**
     * Deterministic hash of the wallet.
     *
     * @return sha256 hash of wallet
     */
    @JsonRpc("sync_hash")
    @POST("/")
    suspend fun walletSyncHash(): String

    /**
     * Apply incoming synchronization data, if provided, and return a sync hash and update wallet
     * data. Wallet must be unlocked to perform this operation. If "encrypt-on-disk" preference is
     * True and supplied password is different from local password, or there is no local password
     * (because local wallet was not encrypted), then the supplied password will be used for local
     * encryption (overwriting previous local encryption password).
     */
    @JsonRpc("sync_apply")
    @POST("/")
    suspend fun applyWalletSyncData(@Body request: ApplyWalletSyncRequest): ApplyWalletSyncResult

    /**
     * Get preference value for key or all values if not key is passed in.
     */
    @JsonRpc("preference_get")
    @POST("/")
    suspend fun preference(): UserPreference

    @JsonRpc("preference_set")
    @POST("/")
    suspend fun setPreference(@Body request: UserPreferenceUpdateRequest): UserPreference

    /**
     * Return an address containing no balance, will create a new address if there is none.
     */
    @JsonRpc("address_unused")
    @POST("/")
    suspend fun addressUnused(): String

    /**
     * List details of all of the accounts or a specific account.
     */
    @JsonRpc("account_list")
    @POST("/")
    suspend fun accounts(): AccountList

    /**
     * List wallets.
     */
    @JsonRpc("wallet_list")
    @POST("/")
    suspend fun wallets(): WalletList

    /**
     * Get lbrynet API server version information
     */
    @JsonRpc("version")
    @POST("/")
    suspend fun version(): LbrynetVersion

    @JsonRpc("wallet_remove")
    @POST("/")
    suspend fun removeWallet(@Body request: WalletRemoveRequest): WalletList.Item

    @JsonRpc("wallet_create")
    @POST("/")
    suspend fun createWallet(@Body request: WalletCreateRequest)

    @JsonRpc("wallet_add")
    @POST("/")
    suspend fun addWallet(@Body request: WalletRemoveRequest)

    @JsonRpc("support_create")
    @POST("/")
    suspend fun supportCreate(@Body request: SupportCreateRequest): SupportCreateResponse

    /**
     * Download stream from a LBRY name.
     */
    @JsonRpc("get")
    @POST("/")
    suspend fun get(@Body request: DownloadRequest): DownloadResponse
}

class LbrynetServiceAuthInterceptor @Inject constructor(
    private val authTokenRepo: AuthTokenRepository,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val authToken = runBlocking { authTokenRepo.authToken() }
        return if (authToken != null) {
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader("X-Lbry-Auth-Token", authToken)
                    .build()
            )
        } else {
            chain.proceed(chain.request())
        }
    }
}

object LbrynetProxyServiceInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain) =
        with(chain.request()) {
            val requestBuilder = newBuilder()
            val newUrl = url.newBuilder()
                .addPathSegments("api/v1/proxy")
                .build()
            return@with requestBuilder.url(newUrl).build()
        }.let(chain::proceed)
}
