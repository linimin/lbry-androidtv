package app.newproj.lbrytv.service

import app.newproj.lbrytv.data.dto.*
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * A JSON-RPC API to interact with the LBRY network.
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
}
