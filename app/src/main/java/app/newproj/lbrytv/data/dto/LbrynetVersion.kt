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

data class LbrynetVersion(
    @SerializedName("build")
    val build: String? = null,

    @SerializedName("desktop")
    val desktop: String? = null,

    @SerializedName("distro")
    val distro: Distro? = null,

    @SerializedName("lbrynet_version")
    val lbrynetVersion: String? = null,

    @SerializedName("os_release")
    val osRelease: String? = null,

    @SerializedName("os_system")
    val osSystem: String? = null,

    @SerializedName("platform")
    val platform: String? = null,

    @SerializedName("processor")
    val processor: String? = null,

    @SerializedName("python_version")
    val pythonVersion: String? = null,

    @SerializedName("version")
    val version: String? = null
) {
    data class Distro(
        @SerializedName("codename")
        val codename: String? = null,

        @SerializedName("id")
        val id: String? = null,

        @SerializedName("like")
        val like: String? = null,

        @SerializedName("version")
        val version: String? = null,

        @SerializedName("version_parts")
        val versionParts: VersionParts? = null
    ) {
        data class VersionParts(
            @SerializedName("build_number")
            val buildNumber: String? = null,

            @SerializedName("major")
            val major: String? = null,

            @SerializedName("minor")
            val minor: String? = null
        )
    }
}
