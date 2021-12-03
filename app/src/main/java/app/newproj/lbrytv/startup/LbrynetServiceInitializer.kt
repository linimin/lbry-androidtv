package app.newproj.lbrytv.startup

import android.content.Context
import androidx.startup.Initializer
import app.newproj.lbrytv.data.repo.InstallationIdRepository
import app.newproj.lbrytv.hiltmodule.ApplicationCoroutineScope
import app.newproj.lbrytv.hiltmodule.LbrynetServiceScope
import app.newproj.lbrytv.service.LbryIncService
import app.newproj.lbrytv.service.LbrynetService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import io.lbry.lbrysdk.ServiceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LbrynetServiceInitializer : Initializer<Unit> {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface HiltEntryPoint {
        @ApplicationCoroutineScope
        fun applicationScope(): CoroutineScope
        fun installationIdRepo(): InstallationIdRepository

        @LbrynetServiceScope
        fun lbrynetService(): LbrynetService
        fun lbryIncService(): LbryIncService
    }

    override fun create(context: Context): Unit {
        val entryPoint = EntryPointAccessors.fromApplication(context, HiltEntryPoint::class.java)
        val installationIdRepo = entryPoint.installationIdRepo()
        entryPoint.applicationScope().launch {
            installationIdRepo.installationId()
            startSdkLbrynetService(context)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    private fun startSdkLbrynetService(context: Context) {
        ServiceHelper.start(
            context,
            "",
            io.lbry.lbrysdk.LbrynetService::class.java,
            "lbrynetservice"
        )
    }
}
