package app.newproj.lbrytv.hiltmodule

import app.newproj.lbrytv.data.dto.LbrynetDaemonStatus
import app.newproj.lbrytv.service.LbrynetService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LbrynetServiceInitJobModule {
    @Provides
    @Singleton
    @LbrynetServiceInitJobScope
    fun lbrynetServiceInitJob(
        @ApplicationCoroutineScope applicationScope: CoroutineScope,
        @LbrynetServiceScope lbrynetService: LbrynetService,
    ): Job {
        return applicationScope.launch {
            awaitLbrynetServiceReady(lbrynetService)
        }
    }

    private tailrec suspend fun awaitLbrynetServiceReady(
        lbrynetService: LbrynetService
    ): LbrynetDaemonStatus {
        return runCatching {
            lbrynetService.daemonStatus()
        }.getOrNull()?.takeIf {
            with(it.startupStatus) { this?.fileManager == true && wallet == true }
        }.also {
            if (it == null) delay(1000)
        } ?: awaitLbrynetServiceReady(lbrynetService)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LbrynetServiceInitJobScope
