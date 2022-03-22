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
import app.newproj.lbrytv.data.dto.JsonRpcResponse
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import kotlin.time.Duration.Companion.seconds

object JsonRpcBodyConverterFactory : Converter.Factory() {
    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): Converter<*, RequestBody>? {
        return RequestBodyConverter(
            rpcMethod = methodAnnotations
                .firstNotNullOfOrNull { it as JsonRpc? }?.method
                ?: return null,
            delegate = retrofit.nextRequestBodyConverter<JsonRpcRequest<*>>(
                this,
                TypeToken.getParameterized(JsonRpcRequest::class.java, type).type,
                parameterAnnotations,
                methodAnnotations
            )
        )
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): Converter<ResponseBody, *> =
        ResponseBodyConverter(
            delegate = retrofit.nextResponseBodyConverter<JsonRpcResponse<*>>(
                this,
                TypeToken.getParameterized(JsonRpcResponse::class.java, type).type,
                annotations
            )
        )

    private class RequestBodyConverter<T>(
        private val rpcMethod: String,
        private val delegate: Converter<JsonRpcRequest<out T>, RequestBody>,
    ) : Converter<T, RequestBody> {
        override fun convert(value: T): RequestBody? =
            delegate.convert(
                JsonRpcRequest(
                    jsonrpc = "2.0",
                    id = System.currentTimeMillis().seconds.inWholeSeconds,
                    method = rpcMethod,
                    params = value
                )
            )
    }

    private class ResponseBodyConverter<T>(
        private val delegate: Converter<ResponseBody, JsonRpcResponse<out T>>,
    ) : Converter<ResponseBody, T> {
        override fun convert(value: ResponseBody): T? = delegate.convert(value)?.result
    }
}
