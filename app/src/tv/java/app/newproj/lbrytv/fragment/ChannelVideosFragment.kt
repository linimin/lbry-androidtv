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

package app.newproj.lbrytv.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.paging.PagingDataAdapter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import app.newproj.lbrytv.NavGraphDirections
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.ItemComparator
import app.newproj.lbrytv.data.dto.Video
import app.newproj.lbrytv.ui.presenter.VideoCardPresenter
import app.newproj.lbrytv.ui.presenter.VideoGridPresenter
import app.newproj.lbrytv.ui.widget.ChannelTitleView
import app.newproj.lbrytv.viewmodel.ChannelVideosViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val NUMBER_OF_COLUMNS = 4

@AndroidEntryPoint
class ChannelVideosFragment : VerticalGridSupportFragment() {
    private val viewModel: ChannelVideosViewModel by viewModels()
    private val channelTitleView get() = titleView as ChannelTitleView?
    private lateinit var videosAdapter: PagingDataAdapter<Video>
    private val navController by lazy { findNavController() }

    override fun onInflateTitleView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.channel_title_view_layout, parent, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = PagingDataAdapter(VideoCardPresenter(), ItemComparator<Video>()).also {
            videosAdapter = it
        }
        gridPresenter = VideoGridPresenter().apply {
            numberOfColumns = NUMBER_OF_COLUMNS
        }
        setOnItemViewClickedListener { _, item, _, _ ->
            require(item is Video)
            goToPlayerScreen(item)
        }

        if (savedInstanceState == null) {
            prepareEntranceTransition()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewLifecycleOwner.lifecycleScope) {
            launch {
                viewModel.uiState.collectLatest { uiState ->
                    when (uiState) {
                        ChannelVideosViewModel.UiState.Loading -> { /* no-op */
                        }
                        is ChannelVideosViewModel.UiState.Idle -> updateUi(uiState)
                        is ChannelVideosViewModel.UiState.Error -> showError(uiState.error)
                    }
                }
            }
            launch {
                videosAdapter.loadStateFlow.collectLatest {
                    when (val refreshLoadState = it.refresh) {
                        LoadState.Loading -> {
                            /* no-op */
                        }
                        is LoadState.NotLoading -> startEntranceTransition()
                        is LoadState.Error -> showError(refreshLoadState.error)
                    }
                }
            }
            launch {
                viewModel.videos().collectLatest(videosAdapter::submitData)
            }
        }
    }

    private fun updateUi(uiState: ChannelVideosViewModel.UiState.Idle) {
        channelTitleView?.setChannel(uiState.channel)
    }

    private fun goToPlayerScreen(video: Video) {
        navController.navigate(NavGraphDirections.actionGlobalVideoPlayerFragment(video.id))
    }

    private fun showError(error: Throwable) {
        navController.navigate(NavGraphDirections.actionGlobalErrorFragment(error.localizedMessage))
    }
}
