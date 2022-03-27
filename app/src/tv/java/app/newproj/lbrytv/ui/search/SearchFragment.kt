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

package app.newproj.lbrytv.ui.search

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.leanback.app.ProgressBarManager
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.ObjectAdapter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import app.newproj.lbrytv.NavGraphDirections
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.BrowseItemUiState
import app.newproj.lbrytv.data.dto.BrowseItemUiStateComparator
import app.newproj.lbrytv.data.dto.ChannelUiState
import app.newproj.lbrytv.data.dto.LocalizableHeaderItem
import app.newproj.lbrytv.data.dto.PagingListRow
import app.newproj.lbrytv.data.dto.VideoUiState
import app.newproj.lbrytv.ui.presenter.BrowseItemUiStatePresenterSelector
import app.newproj.lbrytv.ui.presenter.RowPresenterSelector
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {
    private val viewModel: SearchViewModel by viewModels()
    private val resultRowsAdapter = ArrayObjectAdapter(RowPresenterSelector)
    private lateinit var resultItemsAdapter: PagingDataAdapter<BrowseItemUiState>
    private val progressBarManager = ProgressBarManager()
    private val navController by lazy { findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultItemsAdapter = PagingDataAdapter(
            BrowseItemUiStatePresenterSelector,
            BrowseItemUiStateComparator()
        )
        resultRowsAdapter.add(
            PagingListRow(
                0L,
                LocalizableHeaderItem(0L, null, R.string.search_result),
                resultItemsAdapter,
                viewModel.searchResult
            )
        )
        setOnItemViewClickedListener { _, item, _, _ ->
            require(item is BrowseItemUiState)
            onItemClicked(item)
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackPressed()
        }
        setSearchResultProvider(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        require(view is ViewGroup)
        progressBarManager.setRootView(view)
        viewLifecycleOwner.lifecycleScope.launch {
            resultItemsAdapter.loadStateFlow.collectLatest {
                when (val refreshLoadState = it.refresh) {
                    LoadState.Loading -> progressBarManager.show()
                    is LoadState.NotLoading -> progressBarManager.hide()
                    is LoadState.Error -> showError(refreshLoadState.error.localizedMessage)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressBarManager.setRootView(null)
    }

    override fun getResultsAdapter(): ObjectAdapter = resultRowsAdapter

    override fun onQueryTextChange(newQuery: String?): Boolean = true

    override fun onQueryTextSubmit(query: String?): Boolean {
        viewModel.search(query)
        return true
    }

    private fun onItemClicked(item: BrowseItemUiState) {
        when (item) {
            is VideoUiState -> navController.navigate(
                NavGraphDirections.actionGlobalVideoPlayerFragment(item.id)
            )

            is ChannelUiState -> navController.navigate(
                SearchFragmentDirections.actionSearchFragmentToChannelVideosFragment(item.id)
            )
            else -> throw Exception("Unexpected item: $item")
        }
    }

    private fun showError(message: String?) {
        navController.navigate(NavGraphDirections.actionGlobalErrorFragment(message))
    }

    private fun onBackPressed() {
        val rowViewHolder = rowsSupportFragment
            .findRowViewHolderByPosition(rowsSupportFragment.selectedPosition)
                as ListRowPresenter.ViewHolder?
        if (rowViewHolder != null && rowViewHolder.selectedPosition > 0) {
            rowsSupportFragment.setSelectedPosition(
                0,
                true,
                ListRowPresenter.SelectItemViewHolderTask(0)
            )
        } else {
            navController.popBackStack()
        }
    }
}
