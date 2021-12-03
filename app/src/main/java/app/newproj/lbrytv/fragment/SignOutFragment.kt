package app.newproj.lbrytv.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import app.newproj.lbrytv.R
import app.newproj.lbrytv.viewmodel.LogoutViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val ACTION_ID_LOG_OUT = 0L

@AndroidEntryPoint
class SignOutFragment : GuidedStepSupportFragment() {
    private val viewModel: LogoutViewModel by viewModels()

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(
            getString(R.string.are_you_sure_you_want_to_sign_out_now),
            getString(R.string.you_can_sign_in_again_later),
            getString(R.string.sign_out),
            null
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val action: GuidedAction = GuidedAction.Builder(activity)
            .id(ACTION_ID_LOG_OUT)
            .title(R.string.sign_out)
            .build()
        actions.add(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bindViewModel()
            }
        }
    }

    private suspend fun bindViewModel() {
        viewModel.stateFlow.collectLatest {
            when (it) {
                LogoutViewModel.State.Initial -> Unit
                LogoutViewModel.State.SignOutSuccess -> navigateBackToHome()
                is LogoutViewModel.State.Error -> displayError(it.exception)
            }
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        viewModel.signOut()
    }

    private fun navigateBackToHome() {
        SignOutFragmentDirections
            .actionSignOutFragmentToHomeFragment(true)
            .let {
                findNavController().navigate(it)
            }
    }

    private fun displayError(exception: Exception) {
        SignOutFragmentDirections
            .actionGlobalErrorFragment(exception.localizedMessage)
            .let {
                findNavController().navigate(it)
            }
    }
}
