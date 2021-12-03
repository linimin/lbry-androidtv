package app.newproj.lbrytv.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.Action
import androidx.leanback.widget.Row
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.CardPresentable
import app.newproj.lbrytv.data.dto.ClaimCard
import app.newproj.lbrytv.data.dto.PagingDataListRow
import app.newproj.lbrytv.util.loadImage
import app.newproj.lbrytv.viewmodel.ChannelDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChannelDetailsFragment : DetailsSupportFragment() {
    private val viewModel: ChannelDetailsViewModel by viewModels()
    @Inject lateinit var rowsAdapter: PagingDataAdapter<Row>
    private val backgroundController = DetailsSupportFragmentBackgroundController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            prepareEntranceTransition()
        }
        backgroundController.enableParallax()
        adapter = rowsAdapter
        setOnItemViewClickedListener { _, item, _, _ ->
            when (val card = item as? CardPresentable) {
                is ClaimCard -> onClickedVideo(card)
                else -> Unit
            }
            when ((item as? Action)?.id?.toInt()) {
                R.id.ui_jump_to_latest_videos -> {
                    findLatestVideosRowPosition()?.let(::setSelectedPosition)
                }
                R.id.ui_subscribe -> viewModel.subscribe()
                R.id.ui_unsubscribe -> viewModel.unsubscribe()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBarManager.setRootView(view as ViewGroup)
        val viewLifecycleScope = viewLifecycleOwner.lifecycleScope
        viewLifecycleScope.launch {
            rowsAdapter.loadStateFlow.collectLatest { loadStates ->
                when (val refreshState = loadStates.refresh) {
                    is LoadState.NotLoading -> {
                        startEntranceTransition()
                        progressBarManager.hide()
                    }
                    LoadState.Loading -> progressBarManager.show()
                    is LoadState.Error -> displayError(refreshState.error)
                }
            }
        }
        viewLifecycleScope.launch {
            viewModel.backgroundImageUri.collectLatest {
                backgroundController.loadImage(requireContext(), it)
            }
        }
        viewLifecycleScope.launch {
            viewModel.rows.collectLatest(rowsAdapter::submitData)
        }
    }

    private fun onClickedVideo(card: ClaimCard) {
        ChannelDetailsFragmentDirections
            .actionChannelDetailsFragmentToVideoPlayerFragment(card.claim.id)
            .let(findNavController()::navigate)
    }

    private fun displayError(error: Throwable) {
        ChannelDetailsFragmentDirections
            .actionGlobalErrorFragment(error.localizedMessage)
            .let(findNavController()::navigate)
    }

    private fun findLatestVideosRowPosition(): Int? {
        for (i in 0 until rowsAdapter.size()) {
            val row = rowsAdapter.peek(i)
            if (row is PagingDataListRow && row.pagingDataList.title == R.string.latest_videos) {
                return i
            }
        }
        return null
    }
}
