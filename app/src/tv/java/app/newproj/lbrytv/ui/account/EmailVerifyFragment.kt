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
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.leanback.widget.GuidedActionsStylist
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.newproj.lbrytv.NavGraphDirections
import app.newproj.lbrytv.R
import app.newproj.lbrytv.ui.guidance.id
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmailVerifyFragment : GuidedStepSupportFragment() {
    private val viewModel: EmailVerifyViewModel by viewModels()
    private val navController by lazy { findNavController() }

    override fun onCreateGuidance(savedInstanceState: Bundle?) = GuidanceStylist.Guidance(
        getString(R.string.check_your_email),
        getString(R.string.email_is_sent_click_to_verify),
        getString(R.string.sign_in),
        null
    )

    override fun onCreateActions(actions: MutableList<GuidedAction?>, savedInstanceState: Bundle?) {
        actions.add(
            GuidedAction.Builder(activity)
                .id(R.id.guided_action_processing)
                .infoOnly(false)
                .build()
        )
    }

    override fun onProvideTheme() = R.style.InfoOnlyGuidedStepTheme

    override fun onCreateActionsStylist() = object : GuidedActionsStylist() {
        override fun onProvideItemLayoutId() = R.layout.progress_guided_action
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    if (it.isEmailVerified) {
                        goToBrowseScreen()
                    } else if (it.errorMessage != null) {
                        goToErrorScreen(it.errorMessage)
                    }
                }
        }
    }

    private fun goToBrowseScreen() {
        navController.navigate(
            EmailVerifyFragmentDirections.actionEmailVerifyFragmentToBrowseCategoriesFragment()
        )
    }

    private fun goToErrorScreen(message: String?) {
        navController.navigate(NavGraphDirections.actionGlobalErrorFragment(message = message))
        viewModel.errorMessageShown()
    }
}
