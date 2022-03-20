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

import app.newproj.lbrytv.data.dto.JsonRpc
import app.newproj.lbrytv.data.dto.JsonRpcRequest
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import retrofit2.Invocation
import javax.inject.Inject

class JsonRpcBodyFiller @Inject constructor(private val gson: Gson) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return with(chain.request()) {
            if (body?.contentLength() == 0L) {
                val requestBuilder = newBuilder()
                val rpcMethod = tag(Invocation::class.java)
                    ?.method()
                    ?.getAnnotation(JsonRpc::class.java)
                    ?.method
                rpcMethod?.let {
                    val rpcRequest = JsonRpcRequest("2.0", 0, it, null)
                    val body = gson.toJson(rpcRequest)
                        .toRequestBody("application/json; charset=utf-8".toMediaType())
                    requestBuilder.post(body).build()
                } ?: this
            } else {
                this
            }
        }.let(chain::proceed)
    }
}
