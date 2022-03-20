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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.SupportAmountOption
import app.newproj.lbrytv.data.dto.SupportCreateRequest
import app.newproj.lbrytv.data.dto.SupportOption
import app.newproj.lbrytv.data.repo.SupportAmountRepository
import app.newproj.lbrytv.data.repo.SupportOptionsRepository
import app.newproj.lbrytv.di.ApplicationCoroutineScope
import app.newproj.lbrytv.di.LbrynetProxyService
import app.newproj.lbrytv.fragment.SupportFragmentArgs
import app.newproj.lbrytv.service.LbrynetService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SupportViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    supportOptionsRepo: SupportOptionsRepository,
    amountRepo: SupportAmountRepository,
    @ApplicationCoroutineScope private val externalScope: CoroutineScope,
    @LbrynetProxyService private val lbrynetService: LbrynetService,
) : ViewModel() {
    private val args = SupportFragmentArgs.fromSavedStateHandle(savedStateHandle)

    sealed class UiState {
        data class Initial(
            val supportOptions: List<SupportOption>,
            val amountOptions: List<SupportAmountOption>,
        ) : UiState()

        data class Editing(
            val selectedSupportOptionIndex: Int,
            val selectedAmountOptionIndex: Int,
        ) : UiState()

        object Completed : UiState()
    }

    private val initialState = UiState.Initial(
        supportOptions = supportOptionsRepo.supportOptions(),
        amountOptions = amountRepo.supportAmounts()
    )
    private val _uiState = MutableStateFlow<UiState>(initialState)
    val uiState: StateFlow<UiState> = _uiState

    private val supportTypeIndex: MutableStateFlow<Int> =
        MutableStateFlow(initialState.supportOptions.indexOfFirst { it.isDefault })

    private val amountOptionIndex =
        MutableStateFlow(initialState.amountOptions.indexOfFirst { it.isDefault })

    init {
        viewModelScope.launch {
            supportTypeIndex.combine(amountOptionIndex) { supportTypeIndex, amountOptionIndex ->
                UiState.Editing(supportTypeIndex, amountOptionIndex)
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun setSelectedSupportTypeIndex(index: Int) {
        supportTypeIndex.value = index
    }

    fun setSelectedAmountOptionIndex(index: Int) {
        amountOptionIndex.value = index
    }

    fun send() {
        val isTip = initialState.supportOptions[supportTypeIndex.value].title == R.string.tip
        val amount = initialState.amountOptions[amountOptionIndex.value].quantity.run {
            DecimalFormat("0.0#######", DecimalFormatSymbols(Locale.US))
                .format(this)
        }
        externalScope.launch {
            lbrynetService.supportCreate(
                SupportCreateRequest(
                    args.claimId,
                    amount,
                    blocking = false,
                    isTip = isTip
                )
            )
        }
        _uiState.value = UiState.Completed
    }
}
