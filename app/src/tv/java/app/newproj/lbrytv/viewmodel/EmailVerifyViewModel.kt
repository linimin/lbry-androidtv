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

package app.newproj.lbrytv.viewmodel

import android.accounts.Account
import android.accounts.AccountManager
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.Text
import app.newproj.lbrytv.data.repo.AccountRepository
import app.newproj.lbrytv.fragment.EmailVerifyFragmentArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailVerifyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val accountRepo: AccountRepository,
) : ViewModel() {
    private val args = EmailVerifyFragmentArgs.fromSavedStateHandle(savedStateHandle)

    sealed class UiState {
        data class Processing(val message: Text) : UiState()
        data class Finished(val account: Account) : UiState()
        data class Error(val error: Throwable) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(
        UiState.Processing(Text.Resource(R.string.click_the_verification_link_in_the_email))
    )
    val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch {
            try {
                val account = accountRepo.addAccount(args.email)
                accountRepo.setCurrentAccount(account)
                args.authResponse?.onResult(
                    bundleOf(
                        AccountManager.KEY_ACCOUNT_NAME to account.name,
                        AccountManager.KEY_ACCOUNT_TYPE to account.type,
                    )
                )
                _uiState.value = UiState.Finished(account)
            } catch (error: Throwable) {
                _uiState.value = UiState.Error(error)
            }
        }
    }
}
