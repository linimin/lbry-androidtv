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

package app.newproj.lbrytv.ui.presenter

import android.content.Context
import android.view.ViewGroup
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.RowPresenter
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import app.newproj.lbrytv.data.dto.PagingListRow
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PagingListRowPresenter : ListRowPresenter() {
    init {
        headerPresenter = LocalizableRowHeaderPresenter()
        syncActivatePolicy = SYNC_ACTIVATED_TO_EXPANDED_AND_SELECTED
    }

    override fun createRowViewHolder(parent: ViewGroup): RowPresenter.ViewHolder {
        return (super.createRowViewHolder(parent) as ViewHolder).apply {
            with(gridView) {
                windowAlignmentOffsetPercent = 0f
                val typedArray = parent.context.obtainStyledAttributes(
                    intArrayOf(androidx.leanback.R.styleable.LeanbackTheme_browsePaddingStart)
                )
                windowAlignmentOffset = typedArray.getDimensionPixelSize(
                    androidx.leanback.R.styleable.LeanbackTheme_browsePaddingStart,
                    0
                )
                typedArray.recycle()
                itemAlignmentOffsetPercent = 0f
                clipChildren = false
            }
        }
    }

    override fun isUsingDefaultShadow() = false

    override fun isUsingZOrder(context: Context) = false

    override fun onRowViewAttachedToWindow(viewHolder: RowPresenter.ViewHolder) {
        super.onRowViewAttachedToWindow(viewHolder)
        val row = viewHolder.row as PagingListRow
        val job = viewHolder.view.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
            row.items.collectLatest { pagingData ->
                row.pagingDataAdapter.submitData(pagingData.map { it })
            }
        }
        viewHolder.setFacet(Job::class.java, job)
    }

    override fun onRowViewDetachedFromWindow(viewHolder: RowPresenter.ViewHolder) {
        super.onRowViewDetachedFromWindow(viewHolder)
        (viewHolder.getFacet(Job::class.java) as Job).cancel()
    }
}
