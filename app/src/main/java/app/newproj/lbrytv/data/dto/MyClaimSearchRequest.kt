package app.newproj.lbrytv.data.dto

import com.google.gson.annotations.SerializedName

data class MyClaimSearchRequest(
    /**
     * claim type: channel, stream, repost, collection
     */
    @SerializedName("claim_type")
    val claimTypes: List<ClaimType>? = null,

    /**
     * claim id
     */
    @SerializedName("claim_id")
    val claimIds: List<String>? = null,

    /**
     * streams in this channel
     */
    @SerializedName("channel_id")
    val channelIds: List<String>? = null,

    /**
     * claim name
     */
    @SerializedName("name")
    val names: List<String>? = null,

    /**
     * shows previous claim updates and abandons
     */
    @SerializedName("is_spent")
    val isSpent: Boolean? = null,

    /**
     * id of the account to query
     */
    @SerializedName("account_id")
    val accountId: String? = null,

    /**
     * restrict results to specific wallet
     */
    @SerializedName("wallet_id")
    val walletId: String? = null,

    /**
     * list claims containing a source field
     */
    @SerializedName("has_source")
    val hasSource: Boolean? = null,

    /**
     * list claims not containing a source field
     */
    @SerializedName("has_no_source")
    val hasNoSource: Boolean? = null,

    /**
     * page to return during paginating
     */
    @SerializedName("page")
    val page: Int? = null,

    /**
     * number of items on page during pagination
     */
    @SerializedName("page_size")
    val pageSize: Int? = null,

    /**
     * resolves each claim to provide additional metadata
     */
    @SerializedName("resolve")
    val resolve: Boolean? = null,

    /**
     * field to order by: 'name', 'height', 'amount'
     */
    @SerializedName("order_by")
    val orderBy: String? = null,

    /**
     * do not calculate the total number of pages and items in result set (significant performance boost)
     */
    @SerializedName("no_totals")
    val noTotals: Boolean? = null,

    /**
     * calculate the amount of tips received for claim outputs
     */
    @SerializedName("include_received_tips")
    val includeReceivedTips: Boolean? = null,
)
