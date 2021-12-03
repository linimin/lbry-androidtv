package app.newproj.lbrytv.hiltmodule

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import app.newproj.lbrytv.data.AppSettings
import app.newproj.lbrytv.data.preference.AppSettingsSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppSettingsModule {
    @Provides
    @Singleton
    fun appSettings(@ApplicationContext context: Context): DataStore<AppSettings> {
        return DataStoreFactory.create(
            serializer = AppSettingsSerializer,
            produceFile = { context.dataStoreFile("app-settings.pb") }
        )
    }
}
