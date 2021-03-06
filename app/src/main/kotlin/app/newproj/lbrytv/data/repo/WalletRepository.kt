/*
 * MIT License
 *
 * Copyright (c) 2022 LIN I MIN
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package app.newproj.lbrytv.data.repo

import app.newproj.lbrytv.data.dto.Wallet
import app.newproj.lbrytv.service.LbrynetService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class WalletRepository @Inject constructor(
    private val accountsRepository: AccountsRepository,
    private val lbrynetService: LbrynetService,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun wallet(): Flow<Wallet?> = accountsRepository
        .currentAccount()
        .flatMapLatest { account ->
            flow {
                if (account != null) {
                    while (true) {
                        val totalBalance = lbrynetService.walletBalance().total ?: BigDecimal.ZERO
                        val walletAddress = lbrynetService.addressUnused()
                        emit(Wallet(walletAddress, totalBalance))
                        delay(5.seconds)
                    }
                } else {
                    emit(null)
                }
                delay(5.seconds)
            }
        }
}
