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

package app.newproj.lbrytv.service

import app.newproj.lbrytv.data.dto.JsonRpcResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Interceptor
import okhttp3.Response
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class JsonRpcInterceptor @Inject constructor(private val gson: Gson) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val jsonRpcResponse = gson.fromJson<JsonRpcResponse<Any>>(
            response.body?.source()
                ?.apply { request(Long.MAX_VALUE) }
                ?.buffer?.clone()
                ?.readString(StandardCharsets.UTF_8),
            TypeToken.getParameterized(JsonRpcResponse::class.java, Any::class.java).type
        )
        val httpErrorCode = when (jsonRpcResponse?.error?.code) {
            -32600 -> 400
            -32601 -> 404
            -32700, -32602, -32603, in (-32000 downTo -32099) -> 500
            else -> return response
        }
        return response.newBuilder()
            .code(httpErrorCode)
            .build()
    }
}
