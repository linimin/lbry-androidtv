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

package app.newproj.lbrytv.ui.browsecategories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.viewModels
import androidx.leanback.app.RowsSupportFragment
import androidx.lifecycle.lifecycleScope
import app.newproj.lbrytv.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BrowseCategoryHeaderIconsFragment : RowsSupportFragment() {
    private val viewModel: BrowseCategoriesViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val contextWrapper = ContextThemeWrapper(context, R.style.AppTheme_Overlay_HeaderIcons)
        return super.onCreateView(
            inflater.cloneInContext(contextWrapper),
            container,
            savedInstanceState
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        verticalGridView.setBackgroundColor(requireContext().getColor(R.color.color_surface_80))
        with(viewLifecycleOwner.lifecycleScope) {
            launch {
                viewModel.headersWindowAlignOffsetTop.collectLatest(::setAlignment)
            }
            launch {
                viewModel.selectedHeaderPosition.collectLatest(::setSelectedPosition)
            }
        }
    }
}
