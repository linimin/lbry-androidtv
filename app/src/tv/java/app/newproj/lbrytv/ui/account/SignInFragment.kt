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
import app.newproj.lbrytv.NavGraphDirections
import app.newproj.lbrytv.R
import app.newproj.lbrytv.ui.guidance.id
import app.newproj.lbrytv.ui.guidance.updateAction
import app.newproj.lbrytv.ui.guidance.updateButtonAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : GuidedStepSupportFragment() {
    private val viewModel: SignInViewModel by viewModels()
    private val navController by lazy { findNavController() }

    override fun onCreateGuidance(savedInstanceState: Bundle?) = GuidanceStylist.Guidance(
        getString(R.string.enter_password),
        getString(R.string.signing_in_as, viewModel.uiState.value.email),
        getString(R.string.sign_in),
        null
    )

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.addAll(
            listOf(
                GuidedAction.Builder(context)
                    .id(R.id.guided_action_email)
                    .description(R.string.email)
                    .infoOnly(true)
                    .focusable(false)
                    .build(),
                GuidedAction.Builder(context)
                    .id(R.id.guided_action_password)
                    .title(R.string.enter_password)
                    .editTitle("")
                    .editable(true)
                    .editInputType(
                        InputType.TYPE_CLASS_TEXT
                                or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    )
                    .description(R.string.password)
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
                .enabled(false)
                .build(),
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                updateAction(R.id.guided_action_email) {
                    it.title = uiState.email
                }
                updateButtonAction(GuidedAction.ACTION_ID_CONTINUE) {
                    it.isEnabled = uiState.canContinue
                }
                if (uiState.isSignedIn) {
                    goToBrowseCategoriesScreen()
                } else if (uiState.errorMessage != null) {
                    goToErrorScreen(uiState.errorMessage)
                }
            }
        }
    }

    override fun onGuidedActionEditedAndProceed(action: GuidedAction): Long {
        return if (action.id == R.id.guided_action_password.toLong()) {
            val password = action.editTitle.toString()
            if (viewModel.setPassword(password)) {
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
            viewModel.signIn()
        }
    }

    private fun goToBrowseCategoriesScreen() {
        navController.navigate(
            SignInFragmentDirections.actionSignInFragmentToBrowseCategoriesFragment()
        )
    }

    private fun goToErrorScreen(message: String) {
        navController.navigate(NavGraphDirections.actionGlobalErrorFragment(message = message))
        viewModel.errorMessageShown()
    }
}
