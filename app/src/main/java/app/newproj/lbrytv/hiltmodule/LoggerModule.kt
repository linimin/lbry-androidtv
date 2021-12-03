package app.newproj.lbrytv.hiltmodule

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoggerModule {
    @Provides
    @Singleton
    fun logger(): HttpLoggingInterceptor.Logger {
        return HttpLoggingInterceptor.Logger {
            Timber.tag("OkHttp").d(it)
        }
    }
}
