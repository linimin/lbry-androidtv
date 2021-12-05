package app.newproj.lbrytv.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import androidx.leanback.widget.GuidedActionsStylist
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.newproj.lbrytv.R
import app.newproj.lbrytv.viewmodel.SignInVerifyEmailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception

private const val ACTION_ID_PROCESSING = 0L

@AndroidEntryPoint
class SignInVerifyEmailFragment : GuidedStepSupportFragment() {
    private val viewModel: SignInVerifyEmailViewModel by viewModels()

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(
            getString(R.string.welcome_to_lbry),
            getString(R.string.a_verification_link_has_been_sent_to_your_email_box),
            getString(R.string.sign_in),
            null
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction?>, savedInstanceState: Bundle?) {
        actions.add(
            GuidedAction.Builder(activity)
                .id(ACTION_ID_PROCESSING)
                .title(R.string.click_the_verification_link_in_the_email)
                .infoOnly(true)
                .build()
        )
    }

    override fun onProvideTheme() = R.style.GuidedStepStyle

    override fun onCreateActionsStylist() = object : GuidedActionsStylist() {
        override fun onProvideItemLayoutId() = R.layout.progress_guided_action
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { uiState ->
                when (uiState) {
                    SignInVerifyEmailViewModel.UiState.WaitingForVerification -> Unit
                    SignInVerifyEmailViewModel.UiState.Completed -> onCompleted()
                    is SignInVerifyEmailViewModel.UiState.Error -> onError(uiState.error)
                }
            }
        }
    }

    private fun onCompleted() {
        SignInVerifyEmailFragmentDirections
            .actionSignInVerifyEmailFragmentToHomeFragment()
            .let(findNavController()::navigate)
    }

    private fun onError(error: Exception) {
        SignInVerifyEmailFragmentDirections
            .actionGlobalErrorFragment(error.localizedMessage)
            .let(findNavController()::navigate)
    }
}
