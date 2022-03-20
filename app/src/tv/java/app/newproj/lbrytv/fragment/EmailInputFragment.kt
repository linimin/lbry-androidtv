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
import android.text.InputType
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.newproj.lbrytv.R
import app.newproj.lbrytv.viewmodel.SignInEmailInputViewModel
import app.newproj.lbrytv.viewmodel.SignInEmailInputViewModel.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmailInputFragment : GuidedStepSupportFragment() {
    private val args by navArgs<EmailInputFragmentArgs>()
    private val viewModel: SignInEmailInputViewModel by viewModels()
    private val navController by lazy { findNavController() }

    override fun onCreateGuidance(savedInstanceState: Bundle?) = GuidanceStylist.Guidance(
        getString(R.string.welcome_to_lbry),
        getString(R.string.enter_the_email_address_associated_with_your_existing_lbry_account),
        getString(R.string.sign_in),
        null
    )

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val emailInputAction = GuidedAction.Builder(context)
            .id(R.id.guided_action_email)
            .editable(true)
            .editTitle("")
            .editDescription(R.string.enter_email_address)
            .editInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
            .build()
        val continueAction = GuidedAction.Builder(context)
            .clickAction(GuidedAction.ACTION_ID_CONTINUE)
            .build()
        actions.addAll(listOf(emailInputAction, continueAction))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { uiState ->
                when (uiState) {
                    is UiState.Editing -> {
                        updateAction(R.id.guided_action_email) {
                            it.title = getString(uiState.title)
                            it.description = getString(uiState.description)
                        }
                        updateAction(GuidedAction.ACTION_ID_CONTINUE) {
                            it.isEnabled = uiState.isValidEmail
                        }
                    }
                    is UiState.Finished -> goToNextStep(uiState.email)
                }
            }
        }
    }

    override fun onGuidedActionEditedAndProceed(action: GuidedAction) =
        if (action.id == R.id.guided_action_email.toLong()) {
            if (viewModel.setEmail(action.editTitle.toString())) {
                GuidedAction.ACTION_ID_NEXT
            } else {
                GuidedAction.ACTION_ID_CURRENT
            }
        } else {
            super.onGuidedActionEditedAndProceed(action)
        }

    override fun onGuidedActionClicked(action: GuidedAction) {
        if (action.id == GuidedAction.ACTION_ID_CONTINUE) {
            viewModel.enter()
        }
    }

    private fun goToNextStep(email: String) {
        navController.navigate(
            EmailInputFragmentDirections
                .actionEmailInputFragmentToEmailVerifyFragment(email, args.authResponse)
        )
    }
}
