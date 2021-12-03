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

private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

class JsonRpcEmptyBodyInterceptor @Inject constructor(private val gson: Gson) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return with(chain.request()) {
            if (body?.contentLength() == 0L) {
                tag(Invocation::class.java)
                    ?.method()
                    ?.getAnnotation(JsonRpc::class.java)
                    ?.method?.let { jsonRpcMethod ->
                        val jsonRpcRequest = JsonRpcRequest("2.0", 0, jsonRpcMethod, null)
                        val newBody = gson.toJson(jsonRpcRequest)
                            .toRequestBody(JSON_MEDIA_TYPE)
                        newBuilder().post(newBody).build()
                    } ?: this
            } else {
                this
            }
        }.let(chain::proceed)
    }
}
