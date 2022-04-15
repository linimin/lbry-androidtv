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

import android.accounts.Account
import android.accounts.AccountManager
import androidx.datastore.core.DataStore
import app.newproj.lbrytv.LbryAccountAuthenticator
import app.newproj.lbrytv.data.AppData
import app.newproj.lbrytv.data.AppDatabase
import app.newproj.lbrytv.data.dto.TokenUser
import app.newproj.lbrytv.service.LbryIncService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Provider

private const val ACCOUNT_TYPE = LbryAccountAuthenticator.ACCOUNT_TYPE
private const val AUTH_TOKEN_TYPE = LbryAccountAuthenticator.AUTH_TOKEN_TYPE_DEFAULT
private const val TOKEN_USER_POLLING_PERIOD_MILLIS = 5000L

class AccountsRepository @Inject constructor(
    private val accountManager: AccountManager,
    private val appDataStore: DataStore<AppData>,
    // Use [Provider] to break the dependency cycle.
    private val lbryIncServiceProvider: Provider<LbryIncService>,
    private val installationIdRepo: InstallationIdRepository,
    private val appDatabase: AppDatabase,
) {
    fun accounts(): List<Account> {
        return accountManager.getAccountsByType(ACCOUNT_TYPE).asList()
    }

    suspend fun addAccount(email: String, password: String): Account {
        val installationId = installationIdRepo.installationId()
        val lbryIncService = lbryIncServiceProvider.get()
        val newUser = lbryIncService.newUser("en", installationId)
        val authToken = newUser.plainTextAuthToken
        lbryIncService.signIn(email, password, authToken)
        return Account(email, ACCOUNT_TYPE).also {
            with(accountManager) {
                addAccountExplicitly(it, null, null)
                setAuthToken(it, AUTH_TOKEN_TYPE, authToken)
            }
        }
    }

    suspend fun addAccount(email: String): Account {
        val installationId = installationIdRepo.installationId()
        val lbryIncService = lbryIncServiceProvider.get()
        val newUser = lbryIncService.newUser("en", installationId)
        val authToken = newUser.plainTextAuthToken
        try {
            lbryIncService.registerEmail(email, true, authToken)
        } catch (e: HttpException) {
            if (e.code() == 409) { // This email is already in use.
                lbryIncService.verifyEmail(email, true, authToken)
            } else throw e
        }
        var tokenUser: TokenUser?
        while (true) {
            tokenUser = lbryIncService.tokenUser(authToken)
            if (tokenUser.hasVerifiedEmail == true) {
                break
            }
            delay(TOKEN_USER_POLLING_PERIOD_MILLIS)
        }
        return Account(email, ACCOUNT_TYPE).also {
            with(accountManager) {
                addAccountExplicitly(it, null, null)
                setAuthToken(it, AUTH_TOKEN_TYPE, authToken)
            }
        }
    }

    suspend fun setCurrentAccount(account: Account?) {
        appDataStore.updateData { appData ->
            appData.toBuilder()
                .apply {
                    if (account != null) {
                        accountName = account.name
                    } else {
                        lbryIncServiceProvider.get().signOut()
                        clearAccountName()
                        currentAccount()?.let { account ->
                            accountManager.removeAccountExplicitly(account)
                            appDatabase.subscriptionDao().clearAll(account.name)
                        }
                    }
                }
                .build()
        }
    }

    suspend fun currentAccount(): Account? {
        val accountName = appDataStore.data.first()
            .accountName.takeIf { it.isNotEmpty() }
            ?: return null
        // Make sure the account is still in the list.
        return accounts().find { it.name == accountName }.also { account ->
            if (account == null) {
                appDataStore.updateData { appData ->
                    appData.toBuilder()
                        .clearAccountName()
                        .build()
                }
            }
        }
    }

    suspend fun requireAccount(): Account {
        val account = currentAccount()
        checkNotNull(account)
        return account
    }
}
