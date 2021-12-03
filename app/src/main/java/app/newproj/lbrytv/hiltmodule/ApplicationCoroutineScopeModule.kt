package app.newproj.lbrytv.hiltmodule

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationCoroutineScopeModule {
    /**
     * @see <a href="https://medium.com/androiddevelopers/coroutines-patterns-for-work-that-shouldnt-be-cancelled-e26c40f142ad">Coroutines & Patterns for work that shouldn’t be cancelled</a>
     */
    @Provides
    @Singleton
    @ApplicationCoroutineScope
    fun applicationCoroutineScope() = CoroutineScope(SupervisorJob())
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationCoroutineScope
