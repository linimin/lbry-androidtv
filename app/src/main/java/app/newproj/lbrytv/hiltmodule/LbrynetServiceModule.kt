package app.newproj.lbrytv.hiltmodule

import app.newproj.lbrytv.service.JsonRpcEmptyBodyInterceptor
import app.newproj.lbrytv.service.LbrynetService
import app.newproj.lbrytv.service.JsonRpcHttpBodyConverterFactory
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
object LbrynetServiceModule {
    @Provides
    @Singleton
    @LbrynetServiceScope
    fun lbrynetService(
        jsonRpcEmptyBodyInterceptor: JsonRpcEmptyBodyInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        gsonConverterFactory: GsonConverterFactory,
    ): LbrynetService {
        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(jsonRpcEmptyBodyInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl("http://127.0.0.1:5279/")
            .client(client)
            .addConverterFactory(JsonRpcHttpBodyConverterFactory)
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(LbrynetService::class.java)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LbrynetServiceScope
