package app.newproj.lbrytv.service

import app.newproj.lbrytv.data.dto.LbryIncServiceResponse
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

object LbryIncServiceHttpBodyConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val delegate = retrofit.nextResponseBodyConverter<LbryIncServiceResponse<*>>(
            this,
            TypeToken.getParameterized(LbryIncServiceResponse::class.java, type).type,
            annotations
        )
        return ResponseBodyConverter(type, delegate)
    }

    private class ResponseBodyConverter<T>(
        private val type: Type,
        private val delegate: Converter<ResponseBody, LbryIncServiceResponse<out T>>
    ) : Converter<ResponseBody, T> {
        override fun convert(value: ResponseBody): T? {
            return delegate.convert(value)?.data ?: throw NullDataResponseException()
        }
    }
}
