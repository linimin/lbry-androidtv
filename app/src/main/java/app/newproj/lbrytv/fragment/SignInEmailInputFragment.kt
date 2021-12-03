package app.newproj.lbrytv.fragment

import android.os.Bundle
import android.text.InputType
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
import app.newproj.lbrytv.viewmodel.SignInEmailInputViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val ACTION_EMAIL = 0L

@AndroidEntryPoint
class SignInEmailInputFragment : GuidedStepSupportFragment() {
    private val viewModel: SignInEmailInputViewModel by viewModels()

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(
            getString(R.string.welcome_to_lbry),
            getString(R.string.enter_the_email_address_associated_with_your_lbry_account),
            getString(R.string.sign_in),
            null
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val action = GuidedAction.Builder(activity)
            .id(ACTION_EMAIL)
            .title(getString(R.string.enter_email_address))
            .editTitle("")
            .description(getString(R.string.enter_email_address))
            .editDescription(getString(R.string.email))
            .editable(true)
            .editInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
            .build()
        actions.add(action)
    }

    override fun onCreateButtonActions(
        actions: MutableList<GuidedAction>,
        savedInstanceState: Bundle?
    ) {
        actions.add(
            GuidedAction.Builder(activity)
                .clickAction(GuidedAction.ACTION_ID_CONTINUE)
                .enabled(false)
                .build()
        )
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
        viewModel.stateFlow.collectLatest { uiState ->
            when (uiState) {
                SignInEmailInputViewModel.State.Initial -> Unit
                is SignInEmailInputViewModel.State.EmptyEmail -> {
                    setEmailActionDescription(getString(R.string.enter_email_address))
                }
                is SignInEmailInputViewModel.State.InvalidEmail -> {
                    setContinueActionEnabled(false)
                    setEmailActionDescription(
                        getString(R.string.is_not_a_valid_email_address, uiState.email)
                    )
                }
                is SignInEmailInputViewModel.State.ValidEmail -> {
                    setContinueActionEnabled(true)
                    setEmailActionDescription(uiState.email)
                }
            }
        }
    }

    override fun onGuidedActionEditedAndProceed(action: GuidedAction): Long {
        if (action.id == ACTION_EMAIL) {
            viewModel.setEmail(action.editTitle.toString())
        }
        return GuidedAction.ACTION_ID_NEXT
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        if (action.id == GuidedAction.ACTION_ID_CONTINUE) {
            val uiState = viewModel.stateFlow.value
            if (uiState is SignInEmailInputViewModel.State.ValidEmail) {
                navigateToNextStep(uiState.email)
            }
        }
    }

    private fun setContinueActionEnabled(enabled: Boolean) {
        findButtonActionById(GuidedAction.ACTION_ID_CONTINUE).isEnabled = enabled
        notifyButtonActionChanged(findButtonActionPositionById(GuidedAction.ACTION_ID_CONTINUE))
    }

    private fun setEmailActionDescription(description: String) {
        findActionById(ACTION_EMAIL).description = description
    }

    private fun navigateToNextStep(email: String) {
        SignInEmailInputFragmentDirections
            .actionSignInEmailInputFragmentToSignInVerifyEmailFragment(email)
            .let(findNavController()::navigate)
    }
}
