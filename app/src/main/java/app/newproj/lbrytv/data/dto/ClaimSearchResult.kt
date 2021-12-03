package app.newproj.lbrytv.data.dto

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName

data class ClaimSearchResult(
    @SerializedName("blocked")
    val blocked: Blocked? = null,

    @SerializedName("items")
    val items: List<Item?>? = null,

    /**
     * Page number of the current items.
     */
    @SerializedName("page")
    val page: Int? = null,

    /**
     * Number of items to show on a page.
     */
    @SerializedName("page_size")
    val pageSize: Int? = null
) {
    data class Blocked(
        @SerializedName("channels") val channels: List<Any?>? = null,
        @SerializedName("total") val total: Int? = null
    )

    data class Item(
        /**
         * address of who can spend the txo
         */
        @SerializedName("address")
        @Ignore
        val address: String? = null,

        /**
         * value of the txo as a decimal
         */
        @SerializedName("amount")
        @Ignore
        val amount: String? = null,

        @SerializedName("canonical_url")
        @Ignore
        val canonicalUrl: String? = null,

        /**
         * When type is "claim", "support" or "purchase", this is the claim id.
         */
        @SerializedName("claim_id")
        @ColumnInfo(name = "id")
        val claimId: String,

        /**
         * When type is "claim", this determines if it is "create" or "update".
         */
        @SerializedName("claim_op")
        @Ignore
        val claimOp: String? = null,

        /**
         * number of confirmed blocks
         */
        @SerializedName("confirmations")
        @Ignore
        val confirmations: Int? = null,

        /**
         * block where transaction was recorded
         */
        @SerializedName("height")
        @Ignore
        val height: Int? = null,

        @SerializedName("is_channel_signature_valid")
        @Ignore
        val isChannelSignatureValid: Boolean? = null,

        @SerializedName("meta")
        @Embedded
        val meta: Meta? = null,

        /**
         * When type is 'claim' or 'support', this is the claim name.
         */
        @SerializedName("name")
        @ColumnInfo(name = "name")
        val name: String? = null,

        @SerializedName("normalized_name")
        @Ignore
        val normalizedName: String? = null,

        /**
         * position in the transaction
         */
        @SerializedName("nout")
        @Ignore
        val nout: Int? = null,

        /**
         * When type is "claim" or "support", this is the long permanent claim URL.
         */
        @SerializedName("permanent_url")
        @ColumnInfo(name = "permanent_url")
        val permanentUrl: String? = null,

        @SerializedName("short_url")
        @ColumnInfo(name = "short_url")
        val shortUrl: String? = null,

        /**
         * For signed claims only, metadata of signing channel.
         */
        @SerializedName("signing_channel")
        @Embedded
        val signingChannel: SigningChannel? = null,

        @SerializedName("timestamp")
        @Ignore
        val timestamp: Int? = null,

        /**
         * hash of transaction in hex
         */
        @SerializedName("txid")
        @Ignore
        val txid: String? = null,

        /**
         * one of "claim", "support" or "purchase"
         */
        @SerializedName("type")
        @Ignore
        val type: String? = null,

        /**
         * When type is "claim" or "support" with payload, this is the decoded protobuf payload.
         */
        @SerializedName("value")
        @Embedded
        val value: Value? = null,

        /**
         * Determines the type of the "value" field: "channel", "stream", etc.
         */
        @SerializedName("value_type")
        @ColumnInfo(name = "value_type")
        val valueType: ClaimType? = null
    ) {
        data class Meta(
            @SerializedName("activation_height")
            @Ignore
            val activationHeight: Int? = null,

            @SerializedName("creation_height")
            @Ignore
            val creationHeight: Int? = null,

            @SerializedName("creation_timestamp")
            @Ignore
            val creationTimestamp: Int? = null,

            @SerializedName("effective_amount")
            @ColumnInfo(name = "effective_amount")
            val effectiveAmount: String? = null,

            @SerializedName("expiration_height")
            @Ignore
            val expirationHeight: Int? = null,

            @SerializedName("is_controlling")
            @Ignore
            val isControlling: Boolean? = null,

            @SerializedName("reposted")
            @Ignore
            val reposted: Int? = null,

            @SerializedName("support_amount")
            @Ignore
            val supportAmount: String? = null,

            @SerializedName("take_over_height")
            @Ignore
            val takeOverHeight: Int? = null,

            @SerializedName("trending_global")
            @Ignore
            val trendingGlobal: Double? = null,

            @SerializedName("trending_group")
            @ColumnInfo(name = "trending_group")
            val trendingGroup: Int? = null,

            @SerializedName("trending_local")
            @Ignore
            val trendingLocal: Double? = null,

            @SerializedName("trending_mixed")
            @ColumnInfo(name = "trending_mixed")
            val trendingMixed: Double? = null
        )

        data class SigningChannel(
            @SerializedName("address")
            @Ignore
            val address: String? = null,

            @SerializedName("amount")
            @Ignore
            val amount: String? = null,

            @SerializedName("canonical_url")
            @Ignore
            val canonicalUrl: String? = null,

            @SerializedName("claim_id")
            @ColumnInfo(name = "channel_id")
            val claimId: String? = null,

            @SerializedName("claim_op")
            @Ignore
            val claimOp: String? = null,

            @SerializedName("confirmations")
            @Ignore
            val confirmations: Int? = null,

            @SerializedName("has_signing_key")
            @Ignore
            val hasSigningKey: Boolean? = null,

            @SerializedName("height")
            @Ignore
            val height: Int? = null,

            @SerializedName("meta")
            @Ignore
            val meta: Meta? = null,

            @SerializedName("name")
            @ColumnInfo(name = "channel_name")
            val name: String? = null,

            @SerializedName("normalized_name")
            @Ignore
            val normalizedName: String? = null,

            @SerializedName("nout")
            @Ignore
            val nout: Int? = null,

            @SerializedName("permanent_url")
            @Ignore
            val permanentUrl: String? = null,

            @SerializedName("short_url")
            @Ignore
            val shortUrl: String? = null,

            @SerializedName("timestamp")
            @Ignore
            val timestamp: Int? = null,

            @SerializedName("txid")
            @Ignore
            val txid: String? = null,

            @SerializedName("type")
            @Ignore
            val type: String? = null,

            @SerializedName("value")
            @Embedded
            val value: Value? = null,

            @SerializedName("value_type")
            @Ignore
            val valueType: String? = null
        ) {
            data class Meta(
                @SerializedName("activation_height")
                val activationHeight: Int? = null,

                @SerializedName("claims_in_channel")
                val claimsInChannel: Int? = null,

                @SerializedName("creation_height")
                val creationHeight: Int? = null,

                @SerializedName("creation_timestamp")
                val creationTimestamp: Int? = null,

                @SerializedName("effective_amount")
                val effectiveAmount: String? = null,

                @SerializedName("expiration_height")
                val expirationHeight: Int? = null,

                @SerializedName("is_controlling")
                val isControlling: Boolean? = null,

                @SerializedName("reposted")
                val reposted: Int? = null,

                @SerializedName("support_amount")
                val supportAmount: String? = null,

                @SerializedName("take_over_height")
                val takeOverHeight: Int? = null,

                @SerializedName("trending_global")
                val trendingGlobal: Double? = null,

                @SerializedName("trending_group")
                val trendingGroup: Int? = null,

                @SerializedName("trending_local")
                val trendingLocal: Double? = null,

                @SerializedName("trending_mixed")
                val trendingMixed: Double? = null
            )

            data class Value(
                @SerializedName("cover")
                @Ignore
                val cover: Cover? = null,

                @SerializedName("description")
                @Ignore
                val description: String? = null,

                @SerializedName("locations")
                @Ignore
                val locations: List<Location?>? = null,

                @SerializedName("public_key")
                @Ignore
                val publicKey: String? = null,

                @SerializedName("public_key_id")
                @Ignore
                val publicKeyId: String? = null,

                @SerializedName("tags")
                @Ignore
                val tags: List<String?>? = null,

                @SerializedName("thumbnail")
                @Ignore
                val thumbnail: Thumbnail? = null,

                @SerializedName("title")
                @Ignore
                val title: String? = null
            ) {
                data class Cover(
                    @SerializedName("url")
                    val url: String? = null
                )

                data class Location(
                    @SerializedName("country") val country: String? = null
                )

                data class Thumbnail(
                    @SerializedName("url") val url: String? = null
                )
            }
        }

        data class Value(
            @SerializedName("cover")
            @Embedded
            val cover: Cover?,

            @SerializedName("description")
            @ColumnInfo(name = "description")
            val description: String? = null,

            @SerializedName("languages")
            @Ignore
            val languages: List<String?>? = null,

            @SerializedName("license")
            @Ignore
            val license: String? = null,

            @SerializedName("release_time")
            @ColumnInfo(name = "release_time")
            val releaseTime: String? = null,

            @SerializedName("source")
            @Ignore
            val source: Source? = null,

            @SerializedName("stream_type")
            @Ignore
            val streamType: String? = null,

            @SerializedName("tags")
            @Ignore
            val tags: List<String?>? = null,

            @SerializedName("thumbnail")
            @Embedded
            val thumbnail: Thumbnail? = null,

            @SerializedName("title")
            @ColumnInfo(name = "title")
            val title: String? = null,

            @SerializedName("video")
            @Ignore
            val video: Video? = null
        ) {
            data class Cover(
                @SerializedName("url")
                @ColumnInfo(name = "cover")
                val url: String? = null
            )

            data class Source(
                @SerializedName("hash") val hash: String? = null,
                @SerializedName("media_type") val mediaType: String? = null,
                @SerializedName("name") val name: String? = null,
                @SerializedName("sd_hash") val sdHash: String? = null,
                @SerializedName("size") val size: String? = null
            )

            data class Thumbnail(
                @SerializedName("url")
                @ColumnInfo(name = "thumbnail")
                val url: String? = null
            )

            data class Video(
                @SerializedName("duration") val duration: Int? = null,
                @SerializedName("height") val height: Int? = null,
                @SerializedName("width") val width: Int? = null
            )
        }
    }
}
