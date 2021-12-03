package app.newproj.lbrytv.hiltmodule

import app.newproj.lbrytv.service.JsonRpcHttpBodyConverterFactory
import app.newproj.lbrytv.service.LbrynetService
import app.newproj.lbrytv.service.LbrynetServiceProxyInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LbrynetProxyServiceModule {
    @Provides
    @Singleton
    @LbrynetServiceProxyScope
    fun lbrynetProxyService(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        gsonConverterFactory: GsonConverterFactory,
    ): LbrynetService {
        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(LbrynetServiceProxyInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl("https://api.lbry.tv/")
            .client(client)
            .addConverterFactory(JsonRpcHttpBodyConverterFactory)
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(LbrynetService::class.java)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LbrynetServiceProxyScope
