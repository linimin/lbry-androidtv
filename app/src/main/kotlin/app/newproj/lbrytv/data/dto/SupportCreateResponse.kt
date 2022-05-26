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

data class SupportCreateResponse(
    @SerializedName("height") val height: Int? = null,
    @SerializedName("hex") val hex: String? = null,
    @SerializedName("inputs") val inputs: List<Input?>? = null,
    @SerializedName("outputs") val outputs: List<Output?>? = null,
    @SerializedName("total_fee") val totalFee: String? = null,
    @SerializedName("total_input") val totalInput: String? = null,
    @SerializedName("total_output") val totalOutput: String? = null,
    @SerializedName("txid") val txid: String? = null,
) {
    data class Input(
        @SerializedName("address") val address: String? = null,
        @SerializedName("amount") val amount: String? = null,
        @SerializedName("confirmations") val confirmations: Int? = null,
        @SerializedName("height") val height: Int? = null,
        @SerializedName("nout") val nout: Int? = null,
        @SerializedName("timestamp") val timestamp: Int? = null,
        @SerializedName("txid") val txid: String? = null,
        @SerializedName("type") val type: String? = null,
    )

    data class Output(
        @SerializedName("address") val address: String? = null,
        @SerializedName("amount") val amount: String? = null,
        @SerializedName("claim_id") val claimId: String? = null,
        @SerializedName("confirmations") val confirmations: Int? = null,
        @SerializedName("height") val height: Int? = null,
        @SerializedName("meta") val meta: Meta? = null,
        @SerializedName("name") val name: String? = null,
        @SerializedName("normalized_name") val normalizedName: String? = null,
        @SerializedName("nout") val nout: Int? = null,
        @SerializedName("permanent_url") val permanentUrl: String? = null,
        @SerializedName("timestamp") val timestamp: Any? = null,
        @SerializedName("txid") val txid: String? = null,
        @SerializedName("type") val type: String? = null,
    ) {
        class Meta
    }
}
