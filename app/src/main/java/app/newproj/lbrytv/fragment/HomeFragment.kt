package app.newproj.lbrytv.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.Row
import androidx.leanback.widget.SinglePresenterSelector
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.*
import app.newproj.lbrytv.hiltmodule.LbrynetServiceInitJobScope
import app.newproj.lbrytv.presenter.PagingDataListRowHeaderPresenter
import app.newproj.lbrytv.viewmodel.HomeViewModel
import app.newproj.lbrytv.widget.LbcTitleView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BrowseSupportFragment() {
    private val viewModel: HomeViewModel by viewModels()
    @Inject lateinit var rowsAdapter: PagingDataAdapter<Row>
    private val lbcTitleView get() = titleView as LbcTitleView

    @LbrynetServiceInitJobScope
    @Inject
    lateinit var lbrynetServiceInitJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            prepareEntranceTransition()
        }
        headersState = HEADERS_ENABLED // https://issuetracker.google.com/issues/147614095
        isHeadersTransitionOnBackEnabled = true
        title = getString(R.string.app_name)
        badgeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.logo_with_text_light)
        adapter = rowsAdapter
        setHeaderPresenterSelector(SinglePresenterSelector(PagingDataListRowHeaderPresenter()))
        setOnSearchClickedListener { onClickedSearch() }
        setOnItemViewClickedListener { _, item, _, _ ->
            when (val card = item as? CardPresentable) {
                is ClaimCard -> when (card.claim.valueType) {
                    ClaimType.STREAM_CLAIM -> onClickedVideo(card)
                    ClaimType.CHANNEL_CLAIM -> onClickedChannel(card)
                }
                is SettingCard -> when (card.setting.type) {
                    SettingType.SIGN_IN -> onClickedSignIn()
                    SettingType.SIGN_OUT -> onClickedSignOut()
                }
                else -> Unit
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            viewModel.rows.collectLatest(rowsAdapter::submitData)
        }
        viewLifecycleScope.launch {
            viewModel.totalWalletBalance.collectLatest(lbcTitleView::setWalletBalance)
        }
    }

    private fun onClickedChannel(card: ClaimCard) {
        HomeFragmentDirections
            .actionHomeFragmentToChannelDetailsFragment(card.claim.id)
            .let(findNavController()::navigate)
    }

    private fun onClickedVideo(card: ClaimCard) {
        HomeFragmentDirections
            .actionHomeFragmentToVideoPlayerFragment(card.claim.id)
            .let(findNavController()::navigate)
    }

    private fun onClickedSearch() {
        HomeFragmentDirections
            .actionHomeFragmentToSearchFragment()
            .let(findNavController()::navigate)
    }

    private fun onClickedSignIn() {
        if (lbrynetServiceInitJob.isActive) {
            Toast.makeText(
                requireContext(),
                getString(R.string.the_background_service_is_initializing),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            HomeFragmentDirections
                .actionHomeFragmentToSignInEmailInputFragment()
                .let(findNavController()::navigate)
        }
    }

    private fun onClickedSignOut() {
        HomeFragmentDirections
            .actionHomeFragmentToSignOutFragment()
            .let(findNavController()::navigate)
    }

    private fun displayError(error: Throwable) {
        HomeFragmentDirections
            .actionGlobalErrorFragment(error.localizedMessage)
            .let(findNavController()::navigate)
    }
}
