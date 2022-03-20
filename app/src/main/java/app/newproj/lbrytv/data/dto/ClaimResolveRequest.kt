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
