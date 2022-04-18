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

package app.newproj.lbrytv.ui.support

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.newproj.lbrytv.NavGraphDirections
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.SupportAmountOption
import app.newproj.lbrytv.ui.guidance.findActionById
import app.newproj.lbrytv.ui.guidance.findSubActionById
import app.newproj.lbrytv.ui.guidance.id
import app.newproj.lbrytv.ui.guidance.updateAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SupportFragment : GuidedStepSupportFragment() {
    private val viewModel: SupportViewModel by viewModels()
    private val navController by lazy { findNavController() }

    override fun onCreateGuidance(savedInstanceState: Bundle?) = GuidanceStylist.Guidance(
        getString(R.string.support),
        getString(R.string.tip_and_boost_your_favorite_creators_by_using_your_lbc),
        null,
        null
    )

    override fun onCreateButtonActions(
        actions: MutableList<GuidedAction>,
        savedInstanceState: Bundle?,
    ) {
        actions.addAll(
            listOf(
                GuidedAction.Builder(context)
                    .id(R.id.guided_action_send)
                    .title(R.string.send)
                    .build(),
                GuidedAction.Builder(context)
                    .clickAction(GuidedAction.ACTION_ID_CANCEL)
                    .build()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { uiState ->
                updateActions(uiState.amountOptions)
                uiState.errorMessage?.let(::goToErrorScreen)
            }
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id) {
            R.id.guided_action_send.toLong() -> {
                val isTip = findSubActionById(
                    R.id.guided_action_support_type.toLong(),
                    R.id.tip.toLong()
                )?.isChecked
                val amount = findActionById(R.id.guided_action_amount)
                    ?.subActions
                    ?.indexOfFirst { it.isChecked }
                    ?.let { viewModel.uiState.value.amountOptions[it] }
                    ?.quantity?.toBigDecimal()
                checkNotNull(isTip)
                checkNotNull(amount)
                viewModel.send(amount, isTip)
            }

            GuidedAction.ACTION_ID_CANCEL -> navController.popBackStack()
        }
    }

    override fun onSubGuidedActionClicked(action: GuidedAction): Boolean {
        when (action.checkSetId) {
            R.id.guided_action_check_set_support_type ->
                updateAction(R.id.guided_action_support_type) {
                    it.title = action.title
                }

            R.id.guided_action_check_set_amount ->
                updateAction(R.id.guided_action_amount) {
                    it.title = action.title
                }
        }
        return true
    }

    private fun updateActions(amountOptions: List<SupportAmountOption>) {
        val supportTypeSubActions = listOf(
            GuidedAction.Builder(context)
                .id(R.id.tip)
                .title(R.string.tip)
                .checkSetId(R.id.guided_action_check_set_support_type)
                .checked(true)
                .build(),
            GuidedAction.Builder(context)
                .id(R.id.revocable_support)
                .title(R.string.revocable_support)
                .checkSetId(R.id.guided_action_check_set_support_type)
                .checked(false)
                .build()
        )
        val amountOptionSubActions = amountOptions.map { amountOption ->
            GuidedAction.Builder(context)
                .id(amountOption.id)
                .title(
                    resources.getQuantityString(
                        R.plurals.credit_amount,
                        amountOption.quantity, amountOption.quantity
                    )
                )
                .checkSetId(R.id.guided_action_check_set_amount)
                .checked(amountOption.isDefault)
                .build()
        }
        actions = listOf(
            GuidedAction.Builder(context)
                .id(R.id.guided_action_support_type)
                .title(supportTypeSubActions.find { it.isChecked }?.title)
                .description(R.string.support_type)
                .subActions(supportTypeSubActions)
                .build(),
            GuidedAction.Builder(context)
                .id(R.id.guided_action_amount)
                .title(amountOptionSubActions.find { it.isChecked }?.title)
                .description(R.string.amount)
                .subActions(amountOptionSubActions)
                .build()
        )
    }

    private fun goToErrorScreen(message: String?) {
        navController.navigate(NavGraphDirections.actionGlobalErrorFragment(message))
        viewModel.errorMessageShown()
    }
}
