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

package app.newproj.lbrytv.ui.browsecategories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import app.newproj.lbrytv.data.dto.BrowseCategory
import app.newproj.lbrytv.data.dto.BrowseCategoryUiState
import app.newproj.lbrytv.data.dto.Channel
import app.newproj.lbrytv.data.dto.ChannelUiState
import app.newproj.lbrytv.data.dto.Setting
import app.newproj.lbrytv.data.dto.Video
import app.newproj.lbrytv.data.dto.VideoUiState
import app.newproj.lbrytv.data.dto.Wallet
import app.newproj.lbrytv.data.repo.BrowseCategoriesRepository
import app.newproj.lbrytv.data.repo.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class BrowseCategoriesViewModel @Inject constructor(
    walletRepository: WalletRepository,
    browseCategoriesRepository: BrowseCategoriesRepository,
) : ViewModel() {
    data class UiState(
        val wallet: Wallet? = null,
        val errorMessage: String? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val browseCategories: Flow<PagingData<BrowseCategoryUiState>> =
        browseCategoriesRepository
            .browseCategories()
            .map { it.mapToUiStates() }
            .cachedIn(viewModelScope)

    private val _headersWindowAlignOffsetTop = MutableStateFlow(0)
    val headersWindowAlignOffsetTop: StateFlow<Int> = _headersWindowAlignOffsetTop.asStateFlow()

    private val _selectedHeaderPosition = MutableStateFlow(-1)
    val selectedHeaderPosition: StateFlow<Int> = _selectedHeaderPosition.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                try {
                    _uiState.update {
                        it.copy(wallet = walletRepository.wallet())
                    }
                } catch (error: HttpException) { // pass
                    _uiState.update {
                        it.copy(wallet = null)
                    }
                } finally {
                    delay(5.seconds)
                }
            }
        }
    }

    fun setHeadersAlignment(windowAlignOffsetTop: Int) {
        _headersWindowAlignOffsetTop.tryEmit(windowAlignOffsetTop)
    }

    fun setSelectedHeaderPosition(position: Int) {
        _selectedHeaderPosition.tryEmit(position)
    }

    private fun PagingData<BrowseCategory>.mapToUiStates(): PagingData<BrowseCategoryUiState> =
        map { category ->
            BrowseCategoryUiState(
                id = category.id,
                iconRes = category.iconResId,
                nameRes = category.nameResId,
                items = category.items.map { pagingData ->
                    pagingData.map { browseItem ->
                        when (browseItem) {
                            is Video -> VideoUiState(
                                browseItem.id,
                                browseItem.claim.thumbnail,
                                browseItem.claim.title,
                                browseItem.claim.channelName,
                                browseItem.claim.releaseTime
                            )

                            is Channel -> ChannelUiState(
                                browseItem.id,
                                browseItem.claim.thumbnail,
                                browseItem.claim.title,
                                browseItem.claim.name,
                            )
                            is Setting -> browseItem
                        }
                    }
                }
            )
        }

    fun errorMessageShown() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }
}
