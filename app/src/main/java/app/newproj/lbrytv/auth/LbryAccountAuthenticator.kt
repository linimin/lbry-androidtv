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

package app.newproj.lbrytv.auth

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE
import android.accounts.AccountManager.KEY_ACCOUNT_NAME
import android.accounts.AccountManager.KEY_ACCOUNT_TYPE
import android.accounts.AccountManager.KEY_AUTHTOKEN
import android.accounts.AccountManager.KEY_INTENT
import android.accounts.NetworkErrorException
import android.app.Service
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import app.newproj.lbrytv.data.repo.InstallationIdRepository
import app.newproj.lbrytv.di.ApplicationCoroutineScope
import app.newproj.lbrytv.service.LbryIncService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

class LbryAccountAuthenticator @Inject constructor(
    service: Service,
    private val lbryIncService: LbryIncService,
    private val installationIdRepo: InstallationIdRepository,
    @ApplicationCoroutineScope private val externalScope: CoroutineScope,
) : AbstractAccountAuthenticator(service) {

    override fun editProperties(
        response: AccountAuthenticatorResponse,
        accountType: String,
    ): Bundle {
        throw UnsupportedOperationException()
    }

    override fun addAccount(
        response: AccountAuthenticatorResponse,
        accountType: String,
        authTokenType: String?,
        requiredFeatures: Array<out String>?,
        options: Bundle,
    ): Bundle {
        val uri = Uri.parse("https://lbrytv.newproj.app/signin")
        val intent = Intent(ACTION_VIEW, uri).apply {
            putExtra(KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        }
        return bundleOf(KEY_INTENT to intent)
    }

    override fun confirmCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        options: Bundle?,
    ): Bundle {
        throw UnsupportedOperationException()
    }

    override fun getAuthToken(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        options: Bundle,
    ): Bundle? {
        externalScope.launch {
            try {
                val installationId = installationIdRepo.installationId()
                val newUser = lbryIncService.newUser("en", installationId)
                val result = bundleOf(
                    KEY_ACCOUNT_NAME to account.name,
                    KEY_ACCOUNT_TYPE to account.type,
                    KEY_AUTHTOKEN to newUser.plainTextAuthToken
                )
                response.onResult(result)
            } catch (e: HttpException) {
                throw NetworkErrorException(e)
            }
        }
        return null
    }

    override fun getAuthTokenLabel(authTokenType: String): String? {
        return when (authTokenType) {
            AUTH_TOKEN_TYPE_DEFAULT -> authTokenType
            else -> null
        }
    }

    override fun updateCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String?,
        options: Bundle?,
    ): Bundle {
        throw UnsupportedOperationException()
    }

    override fun hasFeatures(
        response: AccountAuthenticatorResponse,
        account: Account,
        features: Array<out String>,
    ): Bundle {
        throw UnsupportedOperationException()
    }

    companion object {
        const val ACCOUNT_TYPE = "app.newproj.lbrytv"
        const val AUTH_TOKEN_TYPE_DEFAULT = "AUTH_TOKEN_TYPE_DEFAULT"
    }
}
