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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import app.newproj.lbrytv.NavGraphDirections
import app.newproj.lbrytv.R
import app.newproj.lbrytv.ui.guidance.id
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountsFragment : GuidedStepSupportFragment() {
    private val viewModel: AccountsViewModel by viewModels()
    private val navController by lazy { findNavController() }

    override fun onCreateGuidance(savedInstanceState: Bundle?) = GuidanceStylist.Guidance(
        getString(R.string.switch_account),
        null,
        null,
        null
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { uiState ->
                when {
                    uiState.isAccountSelected -> goToBrowseScreen()
                    uiState.accounts?.isEmpty() == true -> goToAccountAddScreen(true)
                    else -> {
                        uiState.accounts?.mapIndexed { index, account ->
                            GuidedAction.Builder(requireContext())
                                .id(index)
                                .icon(R.drawable.person)
                                .title(account.name)
                                .build()
                        }?.run {
                            this + GuidedAction.Builder(requireContext())
                                .id(size)
                                .icon(R.drawable.person_add)
                                .title(R.string.add_another_account)
                                .build()
                        }.let(this@AccountsFragment::setActions)
                        uiState.errorMessage?.let(this@AccountsFragment::goToErrorScreen)
                    }
                }
            }
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        viewModel.uiState.value.accounts
            ?.getOrNull(findActionPositionById(action.id))
            ?.onSelected?.invoke()
            ?: goToAccountAddScreen(false)
    }

    private fun goToAccountAddScreen(popUpSelf: Boolean) {
        navController.navigate(
            directions = AccountsFragmentDirections
                .actionAccountsFragmentToAddAccount(null),
            navOptions = if (popUpSelf) {
                NavOptions.Builder()
                    .setPopUpTo(R.id.accountsFragment, true)
                    .build()
            } else {
                null
            }
        )
    }

    private fun goToBrowseScreen() {
        navController.navigate(
            AccountsFragmentDirections.actionAccountsFragmentToBrowseCategoriesFragment()
        )
    }

    private fun goToErrorScreen(message: String?) {
        navController.navigate(NavGraphDirections.actionGlobalErrorFragment(message))
    }
}
