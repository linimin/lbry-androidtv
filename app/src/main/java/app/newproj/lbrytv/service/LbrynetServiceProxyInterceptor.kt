package app.newproj.lbrytv.service

import okhttp3.Interceptor
import okhttp3.Response

object LbrynetServiceProxyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return with(chain.request()) {
            url.newBuilder()
                .addPathSegments("api/v1/proxy")
                .build()
                .let { newBuilder().url(it).build() }
        }.let(chain::proceed)
    }
}
