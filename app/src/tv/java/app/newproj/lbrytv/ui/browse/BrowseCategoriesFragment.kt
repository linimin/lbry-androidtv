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

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.SinglePresenterSelector
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import app.newproj.lbrytv.NavGraphDirections
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.BrowseItemUiState
import app.newproj.lbrytv.data.dto.ChannelUiState
import app.newproj.lbrytv.data.dto.PagingListRow
import app.newproj.lbrytv.data.dto.RowComparator
import app.newproj.lbrytv.data.dto.Setting
import app.newproj.lbrytv.data.dto.VideoUiState
import app.newproj.lbrytv.databinding.BrowseCategoryHeaderIconsDockBinding
import app.newproj.lbrytv.ui.presenter.IconRowPresenter
import app.newproj.lbrytv.ui.presenter.LocalizableRowHeaderPresenter
import app.newproj.lbrytv.ui.presenter.RowPresenterSelector
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BrowseCategoriesFragment : BrowseSupportFragment() {
    private val viewModel: BrowseCategoriesViewModel by viewModels()
    private val navController by lazy { findNavController() }
    private val walletTitleView get() = titleView as? WalletTitleView
    private val rowsAdapter = PagingDataAdapter(RowPresenterSelector, RowComparator)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Disable the header here and re-enable it in the hidden state in onViewCreated() to
        // workaround this issue: https://issuetracker.google.com/issues/147614095.
        headersState = HEADERS_DISABLED
        setHeaderPresenterSelector(SinglePresenterSelector(LocalizableRowHeaderPresenter()))
        isHeadersTransitionOnBackEnabled = false
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackPressed()
        }
        adapter = rowsAdapter
        setOnSearchClickedListener { goToSearchScreen() }
        setOnItemViewClickedListener { _, item, _, _ ->
            require(item is BrowseItemUiState)
            onBrowseItemClicked(item)
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
        BrowseCategoryHeaderIconsDockBinding
            .inflate(layoutInflater, view, true)
            .browseHeaderIconsDock
            .getFragment<BrowseCategoryHeaderIconsFragment>()
            .apply {
                presenterSelector = SinglePresenterSelector(IconRowPresenter())
                adapter = rowsAdapter
            }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.uiState.collect {
                        walletTitleView?.setWallet(it.wallet)
                        it.errorMessage?.let(::goToErrorScreen).also {
                            viewModel.errorMessageShown()
                        }
                    }
                }
                launch {
                    rowsAdapter.loadStateFlow.collect {
                        when (val refreshLoadState = it.refresh) {
                            LoadState.Loading -> return@collect
                            is LoadState.NotLoading -> startEntranceTransition()
                            is LoadState.Error ->
                                goToErrorScreen(refreshLoadState.error.localizedMessage)
                        }
                    }
                }
                launch {
                    viewModel.browseCategories.collectLatest(rowsAdapter::submitData)
                }
            }
        }
    }

    private fun onBrowseItemClicked(item: BrowseItemUiState) {
        when (item) {
            is VideoUiState -> goToVideoPlayerScreen(item.id)
            is ChannelUiState -> goToChannelVideosScreen(item.id)
            is Setting -> when (item.id) {
                "${R.id.switch_account}" -> goToAccountsScreen()
            }
        }
    }

    private fun onBackPressed() {
        fun refreshSelectedRow() {
            (selectedRowViewHolder?.row as PagingListRow?)?.pagingDataAdapter?.refresh()
        }

        when {
            isShowingHeaders -> startHeadersTransition(false)
            (selectedRowViewHolder as ListRowPresenter.ViewHolder?)?.selectedPosition == 0 -> {
                startHeadersTransition(true)
                refreshSelectedRow()
            }
            else -> {
                setSelectedPosition(
                    selectedPosition,
                    true,
                    ListRowPresenter.SelectItemViewHolderTask(0).apply {
                        isSmoothScroll = false
                    }
                )
                refreshSelectedRow()
            }
        }
    }

    private fun goToSearchScreen() {
        navController.navigate(
            BrowseCategoriesFragmentDirections.actionBrowseCategoriesFragmentToSearchFragment()
        )
    }

    private fun goToVideoPlayerScreen(videoId: String) {
        navController.navigate(NavGraphDirections.actionGlobalVideoPlayerFragment(videoId))
    }

    private fun goToChannelVideosScreen(channelId: String) {
        navController.navigate(
            BrowseCategoriesFragmentDirections
                .actionBrowseCategoriesFragmentToChannelFragment(channelId)
        )
    }

    private fun goToAccountsScreen() {
        navController.navigate(
            BrowseCategoriesFragmentDirections.actionBrowseCategoriesFragmentToAccountsFragment()
        )
    }

    private fun goToErrorScreen(message: String?) {
        navController.navigate(NavGraphDirections.actionGlobalErrorFragment(message))
    }
}
