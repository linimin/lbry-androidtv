package app.newproj.lbrytv.hiltmodule

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.lbry.lbrysdk.Utils
import java.security.KeyStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KeyStoreModule {
    @Provides
    @Singleton
    fun keyStore(@ApplicationContext context: Context): KeyStore {
        return Utils.initKeyStore(context)
    }
}
