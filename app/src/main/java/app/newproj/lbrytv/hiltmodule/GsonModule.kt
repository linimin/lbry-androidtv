package app.newproj.lbrytv.hiltmodule

import android.net.Uri
import app.newproj.lbrytv.data.typeconverter.UriDeserializer
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GsonModule {
    @Provides
    @Singleton
    fun gson(): Gson {
        return Gson().newBuilder()
            .registerTypeAdapter(Uri::class.java, UriDeserializer())
            .create()
    }
}
