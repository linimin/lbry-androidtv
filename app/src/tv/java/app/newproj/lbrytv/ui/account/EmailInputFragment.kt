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

package app.newproj.lbrytv.ui.account

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
import app.newproj.lbrytv.ui.guidance.findSubActionById
import app.newproj.lbrytv.ui.guidance.id
import app.newproj.lbrytv.ui.guidance.updateAction
import app.newproj.lbrytv.ui.guidance.updateButtonAction
import dagger.hilt.android.AndroidEntryPoint
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
        val signInMethodActions = listOf(
            GuidedAction.Builder(requireContext())
                .id(R.id.guided_action_use_magic_link)
                .title(R.string.use_email_link)
                .checkSetId(R.id.check_set_sign_in_method)
                .checked(true)
                .build(),
            GuidedAction.Builder(requireContext())
                .id(R.id.guided_action_use_password)
                .title(R.string.password)
                .checkSetId(R.id.check_set_sign_in_method)
                .build(),
        )
        actions.addAll(
            listOf(
                GuidedAction.Builder(context)
                    .id(R.id.guided_action_email)
                    .editTitle("")
                    .editable(true)
                    .editInputType(
                        InputType.TYPE_CLASS_TEXT
                                or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    )
                    .build(),
                GuidedAction.Builder(context)
                    .id(R.id.guided_action_sign_in_method)
                    .title(R.string.sign_in_method)
                    .title(signInMethodActions.find { it.isChecked }?.title)
                    .description(R.string.sign_in_method)
                    .subActions(signInMethodActions)
                    .build(),
            )
        )
    }

    override fun onCreateButtonActions(
        actions: MutableList<GuidedAction>,
        savedInstanceState: Bundle?,
    ) {
        actions.add(
            GuidedAction.Builder(context)
                .clickAction(GuidedAction.ACTION_ID_CONTINUE)
                .build(),
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                updateAction(R.id.guided_action_email) {
                    it.title = it.editTitle.ifEmpty { getString(R.string.email_address_example) }
                    it.description = if (uiState.isValidEmail) {
                        getString(R.string.enter_email_address)
                    } else {
                        getString(R.string.invalid_email_address)
                    }
                }
                updateButtonAction(GuidedAction.ACTION_ID_CONTINUE) {
                    it.isEnabled = uiState.isValidEmail
                }
            }
        }
    }

    override fun onGuidedActionEditedAndProceed(action: GuidedAction): Long {
        return if (action.id == R.id.guided_action_email.toLong()) {
            if (viewModel.setEmail(action.editTitle.toString())) {
                GuidedAction.ACTION_ID_CONTINUE
            } else {
                GuidedAction.ACTION_ID_CURRENT
            }
        } else {
            super.onGuidedActionEditedAndProceed(action)
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        if (action.id == GuidedAction.ACTION_ID_CONTINUE) {
            check(viewModel.uiState.value.isValidEmail)
            val email = viewModel.uiState.value.email
            if (findSubActionById(R.id.guided_action_use_magic_link)!!.isChecked) {
                goToVerifyScreen(email)
            } else if (findSubActionById(R.id.guided_action_use_password)!!.isChecked) {
                goToSignInScreen(email)
            }
        }
    }

    override fun onSubGuidedActionClicked(action: GuidedAction): Boolean {
        if (action.checkSetId == R.id.check_set_sign_in_method) {
            updateAction(R.id.guided_action_sign_in_method) {
                it.title = action.title
            }
        }
        return true
    }

    private fun goToSignInScreen(email: String) {
        navController.navigate(
            EmailInputFragmentDirections.actionEmailInputFragmentToSignInFragment(
                email,
                args.authResponse
            )
        )
    }

    private fun goToVerifyScreen(email: String) {
        navController.navigate(
            EmailInputFragmentDirections.actionEmailInputFragmentToEmailVerifyFragment(
                email,
                args.authResponse
            )
        )
    }
}
