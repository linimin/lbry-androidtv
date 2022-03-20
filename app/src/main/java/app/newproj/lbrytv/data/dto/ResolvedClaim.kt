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

package app.newproj.lbrytv.data.dto

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName

data class ResolvedClaim(
    /**
     * claim address
     */
    @SerializedName("address")
    @Ignore
    val address: String? = null,

    /**
     * claim amount
     */
    @SerializedName("amount")
    @Ignore
    val amount: String? = null,

    @SerializedName("canonical_url")
    @Ignore
    val canonicalUrl: String? = null,

    /**
     * claim ID
     */
    @SerializedName("claim_id")
    @ColumnInfo(name = "id")
    val claimId: String,

    @SerializedName("claim_op")
    @Ignore
    val claimOp: String? = null,

    /**
     * claim depth
     */
    @SerializedName("confirmations")
    @Ignore
    val confirmations: Int? = null,

    @SerializedName("has_signing_key")
    @Ignore
    val hasSigningKey: Boolean? = null,

    /**
     * claim height
     */
    @SerializedName("height")
    @Ignore
    val height: Int? = null,

    @SerializedName("meta")
    @Embedded
    val meta: Meta? = null,

    /**
     * claim name
     */
    @SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String? = null,

    @SerializedName("normalized_name")
    @Ignore
    val normalizedName: String? = null,

    @SerializedName("nout")
    @Ignore
    val nout: Int? = null,

    /**
     *  permanent url of the claim
     */
    @SerializedName("permanent_url")
    @ColumnInfo(name = "permanent_url")
    val permanentUrl: String? = null,

    @SerializedName("short_url")
    @Ignore
    val shortUrl: String? = null,

    /**
     * timestamp of the block that included this claim tx
     */
    @SerializedName("timestamp")
    @Ignore
    val timestamp: Int? = null,

    /**
     * claim txid
     */
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
    @ColumnInfo(name = "value_type")
    val valueType: ClaimType? = null,
) {
    data class Meta(
        @SerializedName("activation_height")
        @Ignore
        val activationHeight: Int? = null,

        @SerializedName("claims_in_channel")
        @Ignore
        val claimsInChannel: Int? = null,

        @SerializedName("creation_height")
        @Ignore
        val creationHeight: Int? = null,

        @SerializedName("creation_timestamp")
        @Ignore
        val creationTimestamp: Int? = null,

        /**
         * claim amount including supports
         */
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
        val trendingGlobal: Int? = null,

        @SerializedName("trending_group")
        @Ignore
        val trendingGroup: Int? = null,

        @SerializedName("trending_local")
        @Ignore
        val trendingLocal: Int? = null,

        @SerializedName("trending_mixed")
        @Ignore
        val trendingMixed: Int? = null,
    )

    data class Value(
        @SerializedName("cover")
        @Embedded
        val cover: Cover? = null,

        @SerializedName("description")
        @ColumnInfo(name = "description")
        val description: String? = null,

        @SerializedName("email")
        @Ignore
        val email: String? = null,

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
        val tags: List<String>? = null,

        @SerializedName("thumbnail")
        @Embedded
        val thumbnail: Thumbnail? = null,

        @SerializedName("title")
        @ColumnInfo(name = "title")
        val title: String? = null,

        @SerializedName("website_url")
        @Ignore
        val websiteUrl: String? = null,
    ) {
        data class Cover(
            @SerializedName("url")
            @ColumnInfo(name = "cover")
            val url: String? = null,
        )

        data class Location(
            @SerializedName("country")
            @Ignore
            val country: String? = null,
        )

        data class Thumbnail(
            @SerializedName("url")
            @ColumnInfo(name = "thumbnail")
            val url: String? = null,
        )
    }
}
