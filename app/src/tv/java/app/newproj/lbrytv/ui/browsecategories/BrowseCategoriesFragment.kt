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

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.SinglePresenterSelector
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.map
import app.newproj.lbrytv.NavGraphDirections
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.BrowseCategoryUiState
import app.newproj.lbrytv.data.dto.BrowseItemUiState
import app.newproj.lbrytv.data.dto.BrowseItemUiStateComparator
import app.newproj.lbrytv.data.dto.ChannelUiState
import app.newproj.lbrytv.data.dto.LocalizableHeaderItem
import app.newproj.lbrytv.data.dto.PagingListRow
import app.newproj.lbrytv.data.dto.RowComparator
import app.newproj.lbrytv.data.dto.Setting
import app.newproj.lbrytv.data.dto.VideoUiState
import app.newproj.lbrytv.databinding.BrowseCategoryHeaderIconsDockBinding
import app.newproj.lbrytv.ui.presenter.BrowseItemUiStatePresenterSelector
import app.newproj.lbrytv.ui.presenter.IconRowPresenter
import app.newproj.lbrytv.ui.presenter.LocalizableRowHeaderPresenter
import app.newproj.lbrytv.ui.presenter.RowPresenterSelector
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BrowseCategoriesFragment : BrowseSupportFragment() {
    private val viewModel: BrowseCategoriesViewModel by viewModels()
    private val navController by lazy { findNavController() }
    private val rowsAdapter = PagingDataAdapter(RowPresenterSelector, RowComparator)
    private val walletTitleView get() = titleView as? WalletTitleView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = rowsAdapter
        // Disable the header here and re-enable it in the hidden state in onViewCreated() to
        // workaround this issue: https://issuetracker.google.com/issues/147614095.
        headersState = HEADERS_DISABLED
        isHeadersTransitionOnBackEnabled = false
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackPressed()
        }
        setHeaderPresenterSelector(SinglePresenterSelector(LocalizableRowHeaderPresenter()))
        setOnSearchClickedListener { goToSearchScreen() }
        setOnItemViewClickedListener { _, item, _, _ ->
            require(item is BrowseItemUiState)
            handleItemClicked(item)
        }
        if (savedInstanceState == null) {
            prepareEntranceTransition()
        }
    }

    override fun onCreateHeadersSupportFragment() = BrowseCategoryHeadersFragment()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headersState = HEADERS_HIDDEN
        require(view is ViewGroup)
        BrowseCategoryHeaderIconsDockBinding.inflate(layoutInflater, view, true)
        childFragmentManager.findFragmentById(R.id.browse_header_icons_dock).apply {
            require(this is BrowseCategoryHeaderIconsFragment)
            presenterSelector = SinglePresenterSelector(IconRowPresenter())
            adapter = rowsAdapter
        }
        with(viewLifecycleOwner.lifecycleScope) {
            launch {
                viewModel.uiState.collect { uiState ->
                    walletTitleView?.setWallet(uiState.wallet)
                    uiState.errorMessage?.let(::showError)
                }
            }
            launch {
                rowsAdapter.loadStateFlow.collect {
                    when (val refreshLoadState = it.refresh) {
                        LoadState.Loading -> return@collect
                        is LoadState.NotLoading -> startEntranceTransition()
                        is LoadState.Error -> showError(refreshLoadState.error.localizedMessage)
                    }
                }
            }
            launch {
                viewModel.browseCategories
                    .map { it.toRows() }
                    .collectLatest(rowsAdapter::submitData)
            }
        }
    }

    private fun handleItemClicked(item: BrowseItemUiState) {
        when (item) {
            is VideoUiState -> navController.navigate(
                NavGraphDirections.actionGlobalVideoPlayerFragment(item.id)
            )

            is ChannelUiState -> navController.navigate(
                BrowseCategoriesFragmentDirections
                    .actionBrowseCategoriesFragmentToChannelFragment(item.id)
            )

            is Setting -> when (item.titleRes) {
                R.string.switch_account -> navController.navigate(
                    BrowseCategoriesFragmentDirections
                        .actionBrowseCategoriesFragmentToAccountsFragment()
                )
            }
        }
    }

    private fun onBackPressed() {
        when {
            isShowingHeaders -> requireActivity().finish()
            (selectedRowViewHolder as ListRowPresenter.ViewHolder).selectedPosition == 0 ->
                startHeadersTransition(true)

            else -> setSelectedPosition(
                selectedPosition, true,
                ListRowPresenter.SelectItemViewHolderTask(0)
            )
        }
    }

    private fun goToSearchScreen() {
        navController.navigate(
            BrowseCategoriesFragmentDirections.actionBrowseCategoriesFragmentToSearchFragment()
        )
    }

    private fun showError(message: String?) {
        navController.navigate(NavGraphDirections.actionGlobalErrorFragment(message))
        viewModel.errorMessageShown()
    }
}

private fun PagingData<BrowseCategoryUiState>.toRows(): PagingData<Row> = map {
    PagingListRow(
        it.id,
        LocalizableHeaderItem(it.id, it.iconRes, it.nameRes),
        PagingDataAdapter(BrowseItemUiStatePresenterSelector, BrowseItemUiStateComparator()),
        it.items
    )
}
