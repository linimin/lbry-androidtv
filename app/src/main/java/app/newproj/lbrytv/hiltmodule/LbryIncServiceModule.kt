package app.newproj.lbrytv.hiltmodule

import app.newproj.lbrytv.service.LbryIncService
import app.newproj.lbrytv.service.LbryIncServiceAuthInterceptor
import app.newproj.lbrytv.service.LbryIncServiceHttpBodyConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LbryIncServiceModule {
    @Provides
    @Singleton
    fun lbryIncService(
        authInterceptor: LbryIncServiceAuthInterceptor,
        gsonConverterFactory: GsonConverterFactory,
    ): LbryIncService {
        val client = OkHttpClient.Builder()
            .protocols(listOf(Protocol.HTTP_1_1))
            .addInterceptor(authInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl("https://api.lbry.com/")
            .client(client)
            .addConverterFactory(LbryIncServiceHttpBodyConverterFactory)
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(LbryIncService::class.java)
    }
}
