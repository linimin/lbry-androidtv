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

package app.newproj.lbrytv.ui.guidance

import androidx.annotation.IdRes
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidedAction

fun GuidedStepSupportFragment.updateAction(id: Long, update: (GuidedAction) -> Unit) {
    findActionById(id)?.let {
        update(it)
        notifyActionChanged(findActionPositionById(id))
    }
}

fun GuidedStepSupportFragment.updateAction(@IdRes id: Int, update: (GuidedAction) -> Unit) {
    updateAction(id.toLong(), update)
}

fun GuidedStepSupportFragment.findSubActionById(
    parentActionId: Long,
    subActionId: Long,
): GuidedAction? =
    findActionById(parentActionId)?.subActions?.find { it.id == subActionId }

fun GuidedStepSupportFragment.findActionById(@IdRes id: Int): GuidedAction? =
    findActionById(id.toLong())

fun GuidedAction.Builder.id(@IdRes id: Int): GuidedAction.Builder = id(id.toLong())
