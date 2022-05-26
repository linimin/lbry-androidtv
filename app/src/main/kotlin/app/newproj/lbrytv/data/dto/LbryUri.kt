/*
 * MIT License
 *
 * Copyright (c) 2017-2020 LBRY Inc
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

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

class LbryUri private constructor() {
    var path: String? = null
    var isChannel = false
    var streamName: String? = null
    var streamClaimId: String? = null
    var channelName: String? = null
    var channelClaimId: String? = null
    var primaryClaimSequence = 0
    var secondaryClaimSequence = 0
    var primaryBidPosition = 0
    var secondaryBidPosition = 0
    var claimName: String? = null
    var claimId: String? = null
    var contentName: String? = null
    var queryString: String? = null
    val isChannelUrl: Boolean
        get() = channelName.isNullOrEmpty().not() && streamName.isNullOrEmpty() ||
                claimName.isNullOrEmpty().not() && claimName!!.startsWith("@")

    class Builder {
        private val uri = LbryUri()

        fun setChannelName(channelName: String): Builder {
            uri.channelName = channelName
            return this
        }

        fun setClaimId(claimId: String): Builder {
            uri.claimId = claimId
            return this
        }

        fun build() = uri
    }

    private fun build(includeProto: Boolean, protocol: String, vanity: Boolean): String {
        var formattedChannelName: String? = null
        if (channelName != null) {
            formattedChannelName =
                if (channelName!!.startsWith("@")) channelName else String.format(
                    "@%s",
                    channelName
                )
        }
        var primaryClaimName: String? = null
        if ((protocol == LBRY_TV_BASE_URL || protocol == ODYSEE_COM_BASE_URL) && formattedChannelName.isNullOrEmpty()) {
            try {
                primaryClaimName = URLEncoder.encode(claimName, StandardCharsets.UTF_8.name())
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        } else {
            primaryClaimName = claimName
        }
        if (primaryClaimName.isNullOrEmpty()) {
            primaryClaimName = contentName
        }
        if (primaryClaimName.isNullOrEmpty()) {
            primaryClaimName = formattedChannelName
        }
        if (primaryClaimName.isNullOrEmpty()) {
            primaryClaimName = streamName
        }
        var primaryClaimId = claimId
        if (primaryClaimId.isNullOrEmpty()) {
            primaryClaimId =
                if (formattedChannelName.isNullOrEmpty().not()) channelClaimId else streamClaimId
        }
        val sb = StringBuilder()
        if (includeProto) {
            sb.append(protocol)
        }
        sb.append(primaryClaimName)
        if (vanity) {
            return sb.toString()
        }
        var secondaryClaimName: String? = null
        if (claimName.isNullOrEmpty() && contentName.isNullOrEmpty().not()) {
            secondaryClaimName = contentName
        }
        if (secondaryClaimName.isNullOrEmpty()) {
            if (protocol == LBRY_TV_BASE_URL || protocol == ODYSEE_COM_BASE_URL) {
                try {
                    secondaryClaimName =
                        if (formattedChannelName.isNullOrEmpty().not()) URLEncoder.encode(
                            streamName,
                            StandardCharsets.UTF_8.name()
                        ) else null
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
            } else {
                secondaryClaimName =
                    if (formattedChannelName.isNullOrEmpty().not()) streamName else null
            }
        }
        val secondaryClaimId =
            if (secondaryClaimName.isNullOrEmpty().not()) streamClaimId else null
        if (primaryClaimId.isNullOrEmpty().not()) {
            if (protocol == LBRY_TV_BASE_URL || protocol == ODYSEE_COM_BASE_URL) sb.append(':')
                .append(primaryClaimId) else sb.append('#').append(primaryClaimId)
        } else if (primaryClaimSequence > 0) {
            sb.append(':').append(primaryClaimSequence)
        } else if (primaryBidPosition > 0) {
            sb.append('$').append(primaryBidPosition)
        }
        if (secondaryClaimName.isNullOrEmpty().not()) {
            sb.append('/').append(secondaryClaimName)
        }
        if (secondaryClaimId.isNullOrEmpty().not()) {
            if (protocol == LBRY_TV_BASE_URL || protocol == ODYSEE_COM_BASE_URL) sb.append(':')
                .append(secondaryClaimId) else sb.append('#').append(secondaryClaimId)
        } else if (secondaryClaimSequence > 0) {
            sb.append(':').append(secondaryClaimSequence)
        } else if (secondaryBidPosition > 0) {
            sb.append('$').append(secondaryBidPosition)
        }
        return sb.toString()
    }

    fun toTvString(): String {
        return build(true, LBRY_TV_BASE_URL, false)
    }

    fun toOdyseeString(): String {
        return build(true, ODYSEE_COM_BASE_URL, false)
    }

    fun toVanityString(): String {
        return build(true, PROTO_DEFAULT, true)
    }

    override fun toString(): String {
        return build(true, PROTO_DEFAULT, false)
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }

    override fun equals(o: Any?): Boolean {
        return if (o !is LbryUri) {
            false
        } else toString().equals(o.toString(), ignoreCase = true)
    }

    private class UriModifier(val claimId: String?, val claimSequence: Int, val bidPosition: Int) {
        companion object {
            @Throws(LbryUriException::class)
            fun parse(modSeparator: String, modValue: String?): UriModifier {
                var claimId: String? = null
                var claimSequence = 0
                var bidPosition = 0
                if (modSeparator.isNotEmpty()) {
                    if (modValue.isNullOrEmpty()) {
                        throw LbryUriException(
                            String.format(
                                "No modifier provided after separator %s",
                                modSeparator
                            )
                        )
                    }
                    if ("#" == modSeparator || ":" == modSeparator) {
                        claimId = modValue
                    } else if ("*" == modSeparator) {
                        claimSequence = modValue.toIntOrNull() ?: -1
                    } else if ("$" == modSeparator) {
                        bidPosition = modValue.toIntOrNull() ?: -1
                    }
                }
                if (claimId.isNullOrEmpty()
                        .not() && (claimId!!.length > CLAIM_ID_MAX_LENGTH || !claimId.matches(
                        Regex("^[0-9a-f]+$")
                    ))
                ) {
                    throw LbryUriException(String.format("Invalid claim ID %s", claimId))
                }
                if (claimSequence == -1) {
                    throw LbryUriException("Claim sequence must be a number")
                }
                if (bidPosition == -1) {
                    throw LbryUriException("Bid position must be a number")
                }
                return UriModifier(claimId, claimSequence, bidPosition)
            }
        }
    }

    companion object {
        const val LBRY_TV_BASE_URL = "https://lbry.tv/"
        const val ODYSEE_COM_BASE_URL = "https://odysee.com/"
        const val PROTO_DEFAULT = "lbry://"
        const val REGEX_INVALID_URI =
            "[ =&#:$@%?;/\\\\\"<>%\\{\\}|^~\\[\\]`\u0000-\u0008\u000b-\u000c\u000e-\u001F\uD800-\uDFFF\uFFFE-\uFFFF]"
        const val REGEX_ADDRESS = "^(b)(?=[^0OIl]{32,33})[0-9A-Za-z]{32,33}$"
        const val CHANNEL_NAME_MIN_LENGTH = 1
        const val CLAIM_ID_MAX_LENGTH = 40
        private const val REGEX_PART_PROTOCOL = "^((?:lbry://|https://)?)"
        private const val REGEX_PART_HOST =
            "((?:open.lbry.com/|lbry.tv/|lbry.lat/|lbry.fr/|lbry.in/)?)"
        private const val REGEX_PART_STREAM_OR_CHANNEL_NAME = "([^:$#/]*)"
        private const val REGEX_PART_MODIFIER_SEPARATOR = "([:$#]?)([^/]*)"
        private const val QUERY_STRING_BREAKER = "^([\\S]+)([?][\\S]*)"
        private val PATTERN_SEPARATE_QUERY_STRING = Pattern.compile(QUERY_STRING_BREAKER)

        fun isNameValid(name: String?): Boolean {
            return !Pattern.compile(REGEX_INVALID_URI).matcher(name).find()
        }

        fun tryParse(url: String): LbryUri? {
            return try {
                parse(url, false)
            } catch (ex: LbryUriException) {
                null
            }
        }

        @JvmOverloads
        @Throws(LbryUriException::class)
        fun parse(url: String, requireProto: Boolean = false): LbryUri {
            val componentsPattern = Pattern.compile(
                String.format(
                    "%s%s%s%s(/?)%s%s",
                    REGEX_PART_PROTOCOL,
                    REGEX_PART_HOST,
                    REGEX_PART_STREAM_OR_CHANNEL_NAME,
                    REGEX_PART_MODIFIER_SEPARATOR,
                    REGEX_PART_STREAM_OR_CHANNEL_NAME,
                    REGEX_PART_MODIFIER_SEPARATOR
                )
            )
            var cleanUrl = url
            var queryString: String? = null
            if (url.isEmpty()) {
                throw LbryUriException("Invalid url parameter.")
            }
            val qsMatcher = PATTERN_SEPARATE_QUERY_STRING.matcher(url)
            if (qsMatcher.matches()) {
                queryString = qsMatcher.group(2)
                cleanUrl = if (queryString.isNullOrEmpty().not()) url.substring(
                    0,
                    url.indexOf(queryString)
                ) else url
                if (queryString != null && queryString.length > 0) {
                    queryString = queryString.substring(1)
                }
            }
            val components: MutableList<String> = ArrayList()
            val matcher = componentsPattern.matcher(cleanUrl)
            if (matcher.matches()) {
                // Note: For Java regex, group index 0 is always the full match
                for (i in 1..matcher.groupCount()) {
                    components.add(matcher.group(i))
                }
            }
            if (components.size == 0) {
                throw LbryUriException("Regular expression error occurred while trying to parse the value")
            }

            // components[0] = proto
            // components[1] = host
            // components[2] = streamNameOrChannelName
            // components[3] = primaryModSeparator
            // components[4] = primaryModValue
            // components[5] = pathSep
            // components[6] = possibleStreamName
            // components[7] = secondaryModSeparator
            // components[8] = secondaryModValue
            if (requireProto && components[0].isEmpty()) {
                throw LbryUriException("LBRY URLs must include a protocol prefix (lbry://).")
            }
            if (components[2].isEmpty()) {
                throw LbryUriException("URL does not include name.")
            }
            for (component in components.subList(2, components.size)) {
                if (component.indexOf(' ') > -1) {
                    throw LbryUriException("URL cannot include a space.")
                }
            }
            var streamOrChannelName: String? = null
            var possibleStreamName: String? = null
            try {
                // Using java.net.URLDecoder to be able to quickly unit test
                streamOrChannelName =
                    URLDecoder.decode(components[2], StandardCharsets.UTF_8.name())
                possibleStreamName = URLDecoder.decode(components[6], StandardCharsets.UTF_8.name())
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            val primaryModSeparator = components[3]
            val primaryModValue = components[4]
            val secondaryModSeparator = components[7]
            val secondaryModValue = components[8]
            val includesChannel = streamOrChannelName!!.startsWith("@")
            val isChannel = includesChannel && possibleStreamName.isNullOrEmpty()
            val channelName =
                if (includesChannel && streamOrChannelName.length > 1) streamOrChannelName.substring(
                    1
                ) else null
            if (includesChannel) {
                if (channelName.isNullOrEmpty()) {
                    throw LbryUriException("No channel name after @.")
                }
                if (channelName!!.length < CHANNEL_NAME_MIN_LENGTH) {
                    throw LbryUriException(
                        String.format(
                            "Channel names must be at least %d character long.",
                            CHANNEL_NAME_MIN_LENGTH
                        )
                    )
                }
            }
            var primaryMod: UriModifier? = null
            var secondaryMod: UriModifier? = null
            if (primaryModSeparator.isNotEmpty() && primaryModValue.isNotEmpty()) {
                primaryMod = UriModifier.parse(primaryModSeparator, primaryModValue)
            }
            if (secondaryModSeparator.isNotEmpty() && secondaryModValue.isNotEmpty()) {
                secondaryMod = UriModifier.parse(secondaryModSeparator, secondaryModValue)
            }
            val streamName = if (includesChannel) possibleStreamName else streamOrChannelName
            val streamClaimId =
                if (includesChannel && secondaryMod != null) secondaryMod.claimId else primaryMod?.claimId
            val channelClaimId =
                if (includesChannel && primaryMod != null) primaryMod.claimId else null
            val uri = LbryUri()
            uri.isChannel = isChannel
            uri.path = components.subList(2, components.size).joinToString("")
            uri.streamName = streamName
            uri.streamClaimId = streamClaimId
            uri.channelName = channelName
            uri.channelClaimId = channelClaimId
            uri.primaryClaimSequence = primaryMod?.claimSequence ?: -1
            uri.secondaryClaimSequence = secondaryMod?.claimSequence ?: -1
            uri.primaryBidPosition = primaryMod?.bidPosition ?: -1
            uri.secondaryBidPosition = secondaryMod?.bidPosition ?: -1

            // Values that will not work properly with canonical urls
            uri.claimName = streamOrChannelName
            uri.claimId = primaryMod?.claimId
            uri.contentName = streamName
            uri.queryString = queryString
            return uri
        }

        @Throws(LbryUriException::class)
        fun normalize(url: String): String {
            return parse(url).toString()
        }
    }
}

class LbryUriException(message: String?) : Exception(message)
