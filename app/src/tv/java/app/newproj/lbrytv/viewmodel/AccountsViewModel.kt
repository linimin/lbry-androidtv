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

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.Text
import app.newproj.lbrytv.data.repo.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountRepo: AccountRepository,
) : ViewModel() {
    sealed class UiState {
        object Idle : UiState()
        data class AccountsLoaded(val actions: List<UiModel.Account>) : UiState()
        object AddAccount : UiState()
        object Finished : UiState()
    }

    sealed class UiModel {
        data class Account(
            val id: Long,
            @DrawableRes val icon: Int,
            val title: Text,
        ) : UiModel()
    }

    private val _uiState = MutableSharedFlow<UiState>(replay = 2)
    val uiState: SharedFlow<UiState> = _uiState

    private val accounts = accountRepo.accounts()
    private val selectedAccountIndex = MutableStateFlow<Int?>(null)

    init {
        viewModelScope.launch {
            if (accounts.isEmpty()) {
                _uiState.emit(UiState.AccountsLoaded(emptyList()))
            } else {
                _uiState.emit(
                    UiState.AccountsLoaded(
                        accounts.mapIndexed { index, account ->
                            UiModel.Account(index.toLong(),
                                R.drawable.person,
                                Text.Raw(account.name))
                        } + UiModel.Account(
                            accounts.size.toLong(),
                            R.drawable.person_add,
                            Text.Resource(R.string.add_account)
                        )
                    )
                )
            }
            _uiState.emit(UiState.Idle)
            selectedAccountIndex.filterNotNull().collectLatest { index ->
                val account = accounts.getOrNull(index)
                if (account != null) {
                    accountRepo.setCurrentAccount(account)
                    _uiState.emit(UiState.Finished)
                } else {
                    _uiState.emit(UiState.AddAccount)
                }
                selectedAccountIndex.value = null
                _uiState.resetReplayCache()
                _uiState.emit(UiState.Idle)
            }
        }
    }

    fun selectAccountAtIndex(index: Int) {
        selectedAccountIndex.value = index
    }
}
