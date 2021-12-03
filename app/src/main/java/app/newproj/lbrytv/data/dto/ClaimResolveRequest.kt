package app.newproj.lbrytv.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Represents a request to resolve claims.
 */
data class ClaimResolveRequest(
    /**
     * One or more urls to resolve.
     */
    @SerializedName("urls")
    val urls: List<String>,

    /**
     * Wallet to check for claim purchase receipts.
     */
    @SerializedName("wallet_id")
    val walletId: String? = null,

    /**
     * URL of the new SDK server (EXPERIMENTAL).
     */
    @SerializedName("new_sdk_server")
    val newSdkServer: String? = null,

    /**
     * Lookup and include a receipt if this wallet has purchased the claim being resolved.
     */
    @SerializedName("include_purchase_receipt")
    val includePurchaseReceipt: Boolean? = null,

    /**
     * Lookup and include a boolean indicating if claim being resolved is yours.
     */
    @SerializedName("include_is_my_output")
    val includeIsMyOutput: Boolean? = null,

    /**
     * Lookup and sum the total amount of supports you've made to this claim.
     */
    @SerializedName("include_sent_supports")
    val includeSentSupports: Boolean? = null,

    /**
     * Lookup and sum the total amount of tips you've made to this claim (only makes sense when claim is not yours).
     */
    @SerializedName("include_sent_tips")
    val includeSentTips: Boolean? = null,

    /**
     * Lookup and sum the total amount of tips you've received to this claim (only makes sense when claim is yours).
     */
    @SerializedName("include_received_tips")
    val includeReceivedTips: Boolean? = null,
)
