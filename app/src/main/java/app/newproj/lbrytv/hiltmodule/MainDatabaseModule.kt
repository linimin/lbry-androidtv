package app.newproj.lbrytv.hiltmodule

import android.content.Context
import androidx.room.Room
import app.newproj.lbrytv.data.MainDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainDatabaseModule {
    @Provides
    @Singleton
    fun mainDatabase(@ApplicationContext context: Context): MainDatabase {
        return Room.databaseBuilder(
            context,
            MainDatabase::class.java,
            "lbrytv-database"
        ).build()
    }
}
