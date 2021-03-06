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

package app.newproj.lbrytv.ui.browse

import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.Row
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.BrowseCategoryUiState
import app.newproj.lbrytv.data.dto.BrowseItemUiStateComparator
import app.newproj.lbrytv.data.dto.LocalizableHeaderItem
import app.newproj.lbrytv.data.dto.PagingListRow
import app.newproj.lbrytv.data.dto.Wallet
import app.newproj.lbrytv.data.repo.BrowseCategoriesRepository
import app.newproj.lbrytv.data.repo.WalletRepository
import app.newproj.lbrytv.ui.presenter.BrowseItemUiStatePresenterSelector
import app.newproj.lbrytv.usecase.DoOnSubscriptionsChanged
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseCategoriesViewModel @Inject constructor(
    walletRepository: WalletRepository,
    browseCategoriesRepository: BrowseCategoriesRepository,
    private val doOnSubscriptionsChanged: DoOnSubscriptionsChanged,
) : ViewModel() {
    private val _headersWindowAlignOffsetTop = MutableStateFlow(0)
    val headersWindowAlignOffsetTop: StateFlow<Int> = _headersWindowAlignOffsetTop.asStateFlow()

    private val _selectedHeaderPosition = MutableStateFlow(-1)
    val selectedHeaderPosition: StateFlow<Int> = _selectedHeaderPosition.asStateFlow()

    data class UiState(
        val wallet: Wallet? = null,
        val errorMessage: String? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val browseCategories: Flow<PagingData<Row>> =
        browseCategoriesRepository.browseCategories()
            .map { pagingData ->
                pagingData.map { BrowseCategoryUiState.fromBrowseCategory(it) }
            }
            .map { it.toRows() }
            .cachedIn(viewModelScope)

    private val subscriptionVideosAdapter =
        PagingDataAdapter(BrowseItemUiStatePresenterSelector, BrowseItemUiStateComparator())

    private fun PagingData<BrowseCategoryUiState>.toRows(): PagingData<Row> =
        map { category ->
            PagingListRow(
                category.id.toLong(),
                LocalizableHeaderItem(category.id.toLong(), category.iconRes, category.nameRes),
                pagingDataAdapter = if (category.id == R.id.browse_category_subscription_videos) {
                    subscriptionVideosAdapter
                } else {
                    PagingDataAdapter(
                        BrowseItemUiStatePresenterSelector,
                        BrowseItemUiStateComparator()
                    )
                },
                category.items
            )
        }

    init {
        viewModelScope.launch {
            walletRepository.wallet()
                .retryWhen { _, _ -> true }
                .collectLatest { wallet ->
                    _uiState.update {
                        it.copy(wallet = wallet)
                    }
                }
        }
        viewModelScope.launch {
            doOnSubscriptionsChanged {
                subscriptionVideosAdapter.refresh()
            }
        }
    }

    fun setHeadersAlignment(windowAlignOffsetTop: Int) {
        _headersWindowAlignOffsetTop.value = windowAlignOffsetTop
    }

    fun setSelectedHeaderPosition(position: Int) {
        _selectedHeaderPosition.value = position
    }

    fun errorMessageShown() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }
}
