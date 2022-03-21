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

import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.Row
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import app.newproj.lbrytv.data.dto.BrowseCategory
import app.newproj.lbrytv.data.dto.ItemComparator
import app.newproj.lbrytv.data.dto.LocalizableHeaderItem
import app.newproj.lbrytv.data.dto.PagingListRow
import app.newproj.lbrytv.data.dto.Wallet
import app.newproj.lbrytv.data.repo.BrowseCategoryRepository
import app.newproj.lbrytv.data.repo.WalletRepository
import app.newproj.lbrytv.ui.presenter.ItemPresenterSelector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseCategoriesViewModel @Inject constructor(
    walletRepo: WalletRepository,
    categoryRepo: BrowseCategoryRepository,
) : ViewModel() {
    sealed class UiState {
        object Initial : UiState()
        data class Data(val wallet: Wallet) : UiState()
        data class Error(val error: Throwable) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState

    val browseCategories: Flow<PagingData<Row>> = categoryRepo
        .browseCategories()
        .map { it.toRows() }
        .cachedIn(viewModelScope)

    private val _headersWindowAlignOffsetTop = MutableSharedFlow<Int>(replay = 1)
    val headersWindowAlignOffsetTop: SharedFlow<Int> = _headersWindowAlignOffsetTop

    private val _selectedHeaderPosition = MutableSharedFlow<Int>(replay = 1)
    val selectedHeaderPosition: SharedFlow<Int> = _selectedHeaderPosition

    init {
        viewModelScope.launch {
            try {
                walletRepo.wallet().collectLatest {
                    _uiState.value = UiState.Data(it)
                }
            } catch (error: Throwable) {
//                _uiState.value = UiState.Error(error)
            }
        }
    }

    fun setHeadersAlignment(windowAlignOffsetTop: Int) {
        _headersWindowAlignOffsetTop.tryEmit(windowAlignOffsetTop)
    }

    fun setSelectedHeaderPosition(position: Int) {
        _selectedHeaderPosition.tryEmit(position)
    }

    private fun PagingData<BrowseCategory>.toRows(): PagingData<Row> = map { category ->
        PagingListRow(
            category.id,
            LocalizableHeaderItem(category.id, category.iconResId, category.nameResId),
            PagingDataAdapter(ItemPresenterSelector, ItemComparator()),
            category.items
        )
    }
}
