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

package app.newproj.lbrytv.ui.account

import android.accounts.AccountManager
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.newproj.lbrytv.data.repo.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val accountRepository: AccountRepository,
) : ViewModel() {
    private val args = SignInFragmentArgs.fromSavedStateHandle(savedStateHandle)

    data class UiState(
        val email: String,
        val isPasswordFilled: Boolean = false,
        val isProcessing: Boolean = false,
        val isSignedIn: Boolean = false,
        val errorMessage: String? = null,
    ) {
        val canContinue: Boolean get() = isPasswordFilled && isProcessing.not() && isSignedIn.not()
    }

    private val _uiState = MutableStateFlow(UiState(email = args.email))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private var password = ""

    fun setPassword(password: String): Boolean {
        this.password = password
        _uiState.update {
            it.copy(isPasswordFilled = password.isNotEmpty())
        }
        return password.isNotEmpty()
    }

    fun signIn() {
        _uiState.update {
            it.copy(isProcessing = true)
        }
        viewModelScope.launch {
            try {
                val account = accountRepository.addAccount(args.email, password)
                accountRepository.setCurrentAccount(account)
                args.authResponse?.onResult(
                    bundleOf(
                        AccountManager.KEY_ACCOUNT_NAME to account.name,
                        AccountManager.KEY_ACCOUNT_TYPE to account.type,
                    )
                )
                _uiState.update {
                    it.copy(isSignedIn = true, isProcessing = false)
                }
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(errorMessage = error.localizedMessage, isProcessing = false)
                }
            }
        }
    }

    fun errorMessageShown() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }
}
