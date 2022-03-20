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

data class AccountList(
    @SerializedName("items")
    val items: List<Item?>? = null,

    @SerializedName("page")
    val page: Int? = null,

    @SerializedName("page_size")
    val pageSize: Int? = null,

    @SerializedName("total_items")
    val totalItems: Int? = null,

    @SerializedName("total_pages")
    val totalPages: Int? = null
) {
    data class Item(
        @SerializedName("address_generator")
        val addressGenerator: AddressGenerator? = null,

        @SerializedName("certificates")
        val certificates: Int? = null,

        @SerializedName("coins")
        val coins: Double? = null,

        @SerializedName("encrypted")
        val encrypted: Boolean? = null,

        @SerializedName("id")
        val id: String? = null,

        @SerializedName("is_default")
        val isDefault: Boolean? = null,

        @SerializedName("ledger")
        val ledger: String? = null,

        @SerializedName("name")
        val name: String? = null,

        @SerializedName("public_key")
        val publicKey: String? = null,

        @SerializedName("satoshis")
        val satoshis: Int? = null
    ) {
        data class AddressGenerator(
            @SerializedName("change")
            val change: Change? = null,

            @SerializedName("name")
            val name: String? = null,

            @SerializedName("receiving")
            val receiving: Receiving? = null
        ) {
            data class Change(
                @SerializedName("gap")
                val gap: Int? = null,

                @SerializedName("maximum_uses_per_address")
                val maximumUsesPerAddress: Int? = null
            )

            data class Receiving(
                @SerializedName("gap")
                val gap: Int? = null,

                @SerializedName("maximum_uses_per_address")
                val maximumUsesPerAddress: Int? = null
            )
        }
    }
}
