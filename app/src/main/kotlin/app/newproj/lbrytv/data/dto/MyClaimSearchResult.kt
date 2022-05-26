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
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName

data class MyClaimSearchResult(
    @SerializedName("items")
    val items: List<Item?>? = null,

    @SerializedName("page")
    val page: String? = null,

    @SerializedName("page_size")
    val pageSize: String? = null,

    @SerializedName("total_items")
    val totalItems: String? = null,

    @SerializedName("total_pages")
    val totalPages: String? = null
) {
    data class Item(
        @SerializedName("address")
        @Ignore
        val address: String? = null,

        @SerializedName("amount")
        @Ignore
        val amount: String? = null,

        @SerializedName("claim")
        @Ignore
        val claim: String? = null,

        @SerializedName("claim_id")
        @ColumnInfo(name = "id")
        val claimId: String,

        @SerializedName("claim_op")
        @Ignore
        val claimOp: String? = null,

        @SerializedName("confirmations")
        @Ignore
        val confirmations: String? = null,

        @SerializedName("height")
        @Ignore
        val height: String? = null,

        @SerializedName("is_change")
        @Ignore
        val isChange: String? = null,

        @SerializedName("is_channel_signature_valid")
        @Ignore
        val isChannelSignatureValid: String? = null,

        @SerializedName("is_mine")
        @Ignore
        val isMine: String? = null,

        @SerializedName("is_received")
        @Ignore
        val isReceived: String? = null,

        @SerializedName("is_spent")
        @Ignore
        val isSpent: String? = null,

        @SerializedName("name")
        @ColumnInfo(name = "name")
        val name: String? = null,

        @SerializedName("nout")
        @Ignore
        val nout: String? = null,

        @SerializedName("permanent_url")
        @ColumnInfo(name = "permanent_url")
        val permanentUrl: String? = null,

        @SerializedName("protobuf")
        @Ignore
        val protobuf: String? = null,

        @SerializedName("purchase_receipt")
        @Ignore
        val purchaseReceipt: String? = null,

        @SerializedName("reposted_claim")
        @Ignore
        val repostedClaim: String? = null,

        @SerializedName("signing_channel")
        @Ignore
        val signingChannel: String? = null,

        @SerializedName("txid")
        @Ignore
        val txid: String? = null,

        @SerializedName("type")
        @Ignore
        val type: String? = null,

        @SerializedName("value")
        @Ignore
        val value: Value? = null,

        @SerializedName("value_type")
        @ColumnInfo(name = "value_type")
        val valueType: ClaimType? = null
    ) {
        data class Value(
            @SerializedName("public_key")
            @Ignore
            val publicKey: String? = null,

            @SerializedName("public_key_id")
            @Ignore
            val publicKeyId: String? = null,
        )
    }
}
