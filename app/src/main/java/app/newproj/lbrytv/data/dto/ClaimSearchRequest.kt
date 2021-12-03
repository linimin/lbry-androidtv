package app.newproj.lbrytv.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Represents a request to search claims.
 */
data class ClaimSearchRequest(
    /**
     * claim name (normalized)
     */
    @SerializedName("name")
    val name: String? = null,

    /**
     * full text search
     */
    @SerializedName("text")
    val text: String? = null,

    /**
     * full or partial claim id
     */
    @SerializedName("claim_id")
    val claimId: String? = null,

    /**
     * list of full claim ids
     */
    @SerializedName("claim_ids")
    val claimIds: List<String>? = null,

    /**
     * transaction id
     */
    @SerializedName("txid")
    val transactionId: String? = null,

    /**
     * position in the transaction
     */
    @SerializedName("nout")
    val positionInTransaction: String? = null,

    /**
     * Claims signed by this channel (argument is a URL which automatically gets resolved),
     * see [channelIds] if you need to filter by multiple channels at the same time,
     * includes claims with invalid signatures, use in conjunction with [validChannelSignature].
     */
    @SerializedName("channel")
    val channel: String? = null,

    /**
     * Claims signed by any of these channels (arguments must be claim ids of the channels),
     * includes claims with invalid signatures, implies [hasChannelSignature], use in conjunction
     * with [validChannelSignature].
     */
    @SerializedName("channel_ids")
    val channelIds: List<String>? = null,

    /**
     * exclude claims signed by any of these channels (arguments must be claim ids of the channels)
     */
    @SerializedName("not_channel_ids")
    val notChannelIds: List<String>? = null,

    /**
     * claims with a channel signature (valid or invalid)
     */
    @SerializedName("has_channel_signature")
    val hasChannelSignature: Boolean? = null,

    /**
     * Claims with a valid channel signature or no signature, use in conjunction with
     * [hasChannelSignature] to only get claims with valid signatures.
     */
    @SerializedName("valid_channel_signature")
    val validChannelSignature: Boolean? = null,

    /**
     * Claims with invalid channel signature or no signature, use in conjunction with
     * [hasChannelSignature] to only get claims with invalid signatures.
     */
    @SerializedName("invalid_channel_signature")
    val invalidChannelSignature: Boolean? = null,

    /**
     * Only return up to the specified number of claims per channel.
     */
    @SerializedName("limit_claims_per_channel")
    val limitClaimsPerChannel: Int? = null,

    /**
     * winning claims of their respective name
     */
    @SerializedName("is_controlling")
    val isControlling: Boolean? = null,

    /**
     * Only return channels having this public key id, this is the same key as used in the wallet
     * file to map channel certificate private keys: {'public_key_id': 'private key'}.
     */
    @SerializedName("public_key_id")
    val publicKeyId: String? = null,

    /**
     * last updated block height (supports equality constraints)
     */
    @SerializedName("height")
    val height: String? = null,

    /**
     * last updated timestamp (supports equality constraints)
     */
    @SerializedName("timestamp")
    val timestamp: String? = null,

    /**
     * created at block height (supports equality constraints)
     */
    @SerializedName("creation_height")
    val creationHeight: String? = null,

    /**
     * created at timestamp (supports equality constraints)
     */
    @SerializedName("creation_timestamp")
    val creation_timestamp: String? = null,

    /**
     * height at which claim starts competing for name (supports equality constraints)
     */
    @SerializedName("activation_height")
    val activationHeight: String? = null,

    /**
     * height at which claim will expire (supports equality constraints)
     */
    @SerializedName("expiration_height")
    val expirationHeight: String? = null,

    /**
     * Limit to claims self-described as having been released to the public on or after this UTC
     * timestamp, when claim does not provide a release time the publish time is used instead
     * (supports equality constraints).
     */
    @SerializedName("release_time")
    val releaseTime: String? = null,

    /**
     * Limit by claim value (supports equality constraints).
     */
    @SerializedName("amount")
    val amount: String? = null,

    /**
     * Limit by supports and tips received (supports equality constraints).
     */
    @SerializedName("support_amount")
    val supportAmount: String? = null,

    /**
     * Limit by total value (initial claim value plus all tips and supports received), this amount
     * is blank until claim has reached activation height (supports equality constraints).
     */
    @SerializedName("effective_amount")
    val effective_amount: String? = null,

    /**
     * Group numbers 1 through 4 representing the trending groups of the content: 4 means content is
     * trending globally and independently, 3 means content is not trending globally but is trending
     * independently (locally), 2 means it is trending globally but not independently and 1 means
     * it's not trending globally or locally (supports equality constraints).
     */
    @SerializedName("trending_group")
    val trendingGroup: String? = null,

    /**
     * Trending amount taken from the global or local value depending on the trending group:
     * 4 - global value,
     * 3 - local value,
     * 2 - global value,
     * 1 - local value
     * (supports equality constraints).
     */
    @SerializedName("trending_mixed")
    val trendingMixed: String? = null,

    /**
     * trending value calculated relative only to the individual contents past history
     * (supports equality constraints)
     */
    @SerializedName("trending_local")
    val trendingLocal: Int? = null,

    /**
     * trending value calculated relative to all trending content globally
     * (supports equality constraints)
     */
    @SerializedName("trending_global")
    val trendingGlobal: String? = null,

    /**
     * all reposts of the specified original claim id
     */
    @SerializedName("reposted_claim_id")
    val repostedClaimId: String? = null,

    /**
     * claims reposted this many times (supports equality constraints)
     */
    @SerializedName("reposted")
    val reposted: String? = null,

    /**
     * Filter by 'channel', 'stream', 'repost' or 'collection'.
     */
    @SerializedName("claim_type")
    val claimType: List<String>? = null,

    /**
     * Filter by 'video', 'image', 'document', etc.
     */
    @SerializedName("stream_types")
    val streamTypes: List<String>? = null,

    /**
     * Filter by 'video/mp4', 'image/png', etc.
     */
    @SerializedName("media_types")
    val mediaTypes: List<String>? = null,

    /**
     * Specify fee currency: LBC, BTC, USD.
     */
    @SerializedName("fee_currency")
    val feeCurrency: String? = null,

    /**
     * content download fee (supports equality constraints)
     */
    @SerializedName("fee_amount")
    val feeAmount: String? = null,

    /**
     * duration of video or audio in seconds (supports equality constraints)
     */
    @SerializedName("duration")
    val duration: String? = null,

    /**
     * Find claims containing any of the tags.
     */
    @SerializedName("any_tags")
    val anyTags: List<String>? = null,

    /**
     * Find claims containing every tag.
     */
    @SerializedName("all_tags")
    val allTags: List<String>? = null,

    /**
     * Find claims not containing any of these tags.
     */
    @SerializedName("not_tags")
    val notTags: List<String>? = null,

    /**
     * Find claims containing any of the languages.
     */
    @SerializedName("any_languages")
    val anyLanguages: List<String>? = null,

    /**
     * Find claims containing every language.
     */
    @SerializedName("all_languages")
    val allLanguages: List<String>? = null,

    /**
     * Find claims not containing any of these languages.
     */
    @SerializedName("not_languages")
    val notLanguages: List<String>? = null,

    /**
     * Find claims containing any of the locations.
     */
    @SerializedName("any_locations")
    val anyLocations: List<String>? = null,

    /**
     * Find claims containing every location.
     */
    @SerializedName("all_locations")
    val allLocations: List<String>? = null,

    /**
     * Find claims not containing any of these locations.
     */
    @SerializedName("not_locations")
    val notLocations: List<String>? = null,

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
     * Field to order by, default is descending order, to do an ascending order prepend ^ to the
     * field name, eg. '^amount' available fields: 'name', 'height', 'release_time', 'publish_time',
     * 'amount', 'effective_amount', 'support_amount', 'trending_group', 'trending_mixed',
     * 'trending_local', 'trending_global', 'activation_height'.
     */
    @SerializedName("order_by")
    val orderBy: List<String>? = null,

    /**
     * Do not calculate the total number of pages and items in result set
     * (significant performance boost).
     */
    @SerializedName("no_totals")
    val noTotals: Boolean? = true,

    /**
     * wallet to check for claim purchase receipts
     */
    @SerializedName("wallet_id")
    val walletId: String? = null,

    /**
     * Lookup and include a receipt if this wallet has purchased the claim.
     */
    @SerializedName("include_purchase_receipt")
    val includePurchaseReceipt: Boolean? = null,

    /**
     * Lookup and include a boolean indicating if claim being resolved is yours.
     */
    @SerializedName("include_is_my_output")
    val includeIsMyOutput: Boolean? = null,

    /**
     * Removes duplicated content from search by picking either the original claim or the oldest
     * matching repost.
     */
    @SerializedName("remove_duplicates")
    val removeDuplicates: Boolean? = null,

    /**
     * Find claims containing a source field.
     */
    @SerializedName("has_source")
    val hasSource: Boolean? = null,

    /**
     * Find claims not containing a source field.
     */
    @SerializedName("has_no_source")
    val hasNoSource: Boolean? = null,

    /**
     * URL of the new SDK server (EXPERIMENTAL)
     */
    @SerializedName("new_sdk_server")
    val newSdkServer: String? = null,
)
