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

object JsonRpcHttpBodyConverterFactory : Converter.Factory() {
    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        val rpcMethod =
            (methodAnnotations.find { it is JsonRpc } as JsonRpc?)?.method ?: return null
        val delegate = retrofit.nextRequestBodyConverter<JsonRpcRequest<*>>(
            this,
            TypeToken.getParameterized(JsonRpcRequest::class.java, type).type,
            parameterAnnotations,
            methodAnnotations
        )
        return RequestBodyConverter(rpcMethod, delegate)
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val delegate = retrofit.nextResponseBodyConverter<JsonRpcResponse<*>>(
            this,
            TypeToken.getParameterized(JsonRpcResponse::class.java, type).type,
            annotations
        )
        return ResponseBodyConverter(delegate)
    }

    private class RequestBodyConverter<T>(
        private val method: String,
        private val delegate: Converter<JsonRpcRequest<out T>, RequestBody>
    ) : Converter<T, RequestBody> {
        override fun convert(value: T): RequestBody? {
            val id = System.currentTimeMillis() / 1000
            return delegate.convert(JsonRpcRequest("2.0", id, method, value))
        }
    }

    private class ResponseBodyConverter<T>(
        private val delegate: Converter<ResponseBody, JsonRpcResponse<out T>>
    ) : Converter<ResponseBody, T> {
        override fun convert(value: ResponseBody): T? {
            return delegate.convert(value)?.result ?: throw NullDataResponseException()
        }
    }
}
