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
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.SupportAmountOption
import app.newproj.lbrytv.data.dto.SupportOption
import app.newproj.lbrytv.viewmodel.SupportViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SupportFragment : GuidedStepSupportFragment() {
    private val viewModel: SupportViewModel by viewModels()
    private val navController by lazy { findNavController() }

    override fun onCreateGuidance(savedInstanceState: Bundle?) = GuidanceStylist.Guidance(
        getString(R.string.support_this_content),
        getString(R.string.tip_and_boost_your_favorite_creators_by_using_your_lbc),
        null,
        null
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { uiState ->
                when (uiState) {
                    is SupportViewModel.UiState.Initial ->
                        loadActions(uiState.supportOptions, uiState.amountOptions)

                    is SupportViewModel.UiState.Editing ->
                        updateActions(uiState.selectedSupportOptionIndex,
                            uiState.selectedAmountOptionIndex)

                    SupportViewModel.UiState.Completed -> navController.popBackStack()
                }
            }
        }
    }

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

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id) {
            R.id.guided_action_send.toLong() -> viewModel.send()
            GuidedAction.ACTION_ID_CANCEL -> navController.popBackStack()
        }
    }

    override fun onSubGuidedActionClicked(action: GuidedAction): Boolean {
        when (action.checkSetId) {
            R.id.guided_action_check_set_support_type ->
                findSubActionPosition(R.id.guided_action_support_type, action.id)
                    .let(viewModel::setSelectedSupportTypeIndex)

            R.id.guided_action_check_set_amount ->
                findSubActionPosition(R.id.guided_action_amount, action.id)
                    .let(viewModel::setSelectedAmountOptionIndex)
        }
        return true
    }

    private fun loadActions(
        supportOptions: List<SupportOption>,
        amountOptions: List<SupportAmountOption>,
    ) {
        actions = listOf(
            GuidedAction.Builder(context)
                .id(R.id.guided_action_support_type)
                .title(
                    supportOptions
                        .find { it.isDefault }?.title
                        ?: R.string.support_type
                )
                .description(R.string.support_type)
                .subActions(
                    supportOptions.map {
                        GuidedAction.Builder(requireContext())
                            .id(it.id)
                            .title(it.title)
                            .checkSetId(R.id.guided_action_check_set_support_type)
                            .checked(it.isDefault)
                            .build()
                    }
                )
                .build(),
            GuidedAction.Builder(context)
                .id(R.id.guided_action_amount)
                .title(
                    amountOptions
                        .find { it.isDefault }?.let {
                            resources.getQuantityString(it.title, it.quantity, it.quantity)
                        }
                        ?: getString(R.string.amount)
                )
                .description(R.string.amount)
                .subActions(
                    amountOptions.map {
                        GuidedAction.Builder(requireContext())
                            .id(it.id)
                            .title(
                                resources.getQuantityString(
                                    it.title,
                                    it.quantity, it.quantity
                                )
                            )
                            .checkSetId(R.id.guided_action_check_set_amount)
                            .checked(it.isDefault)
                            .build()
                    }
                )
                .build()
        )
    }

    private fun updateActions(selectedSupportTypeIndex: Int, selectedAmountIndex: Int) {
        listOf(
            R.id.guided_action_support_type to selectedSupportTypeIndex,
            R.id.guided_action_amount to selectedAmountIndex,
        ).forEach { pair ->
            updateAction(pair.first) {
                it.title = it.subActions[pair.second].run {
                    isChecked = true
                    title
                }
            }
        }
    }
}
