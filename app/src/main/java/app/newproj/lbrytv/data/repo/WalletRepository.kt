package app.newproj.lbrytv.data.repo

import app.newproj.lbrytv.data.dto.WalletBalance
import app.newproj.lbrytv.hiltmodule.LbrynetServiceInitJobScope
import app.newproj.lbrytv.hiltmodule.LbrynetServiceScope
import app.newproj.lbrytv.service.LbrynetService
import kotlinx.coroutines.Job
import javax.inject.Inject

class WalletRepository @Inject constructor(
    @LbrynetServiceScope private val lbrynetService: LbrynetService,
    @LbrynetServiceInitJobScope private val lbrynetServiceInitJob: Job,
) {
    suspend fun walletBalance(): WalletBalance {
        lbrynetServiceInitJob.join()
        return lbrynetService.walletBalance()
    }
}
