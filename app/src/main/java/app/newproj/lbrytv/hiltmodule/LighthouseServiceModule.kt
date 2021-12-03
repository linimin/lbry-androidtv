package app.newproj.lbrytv.hiltmodule

import app.newproj.lbrytv.service.LighthouseService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LighthouseServiceModule {
    @Provides
    @Singleton
    fun lighthouseService(
        gsonConverterFactory: GsonConverterFactory,
    ): LighthouseService {
        return Retrofit.Builder()
            .baseUrl("https://lighthouse.lbry.com")
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(LighthouseService::class.java)
    }
}
