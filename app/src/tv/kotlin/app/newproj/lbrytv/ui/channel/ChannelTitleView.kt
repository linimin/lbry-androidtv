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

package app.newproj.lbrytv.ui.channel

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.leanback.widget.TitleViewAdapter
import app.newproj.lbrytv.data.dto.ChannelUiState
import app.newproj.lbrytv.databinding.ChannelTitleViewBinding

class ChannelTitleView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs), TitleViewAdapter.Provider {
    private val titleViewAdapter = object : TitleViewAdapter() {
        override fun getSearchAffordanceView() = binding.searchOrb

        override fun updateComponentsVisibility(flags: Int) {
            val brandingViewIsVisible = flags.and(BRANDING_VIEW_VISIBLE) == BRANDING_VIEW_VISIBLE
            val searchOrbIsVisible = flags.and(SEARCH_VIEW_VISIBLE) == SEARCH_VIEW_VISIBLE
            with(binding) {
                infoLayout.isVisible = brandingViewIsVisible
                logo.isVisible = brandingViewIsVisible
                searchOrb.isVisible = searchOrbIsVisible && onSearchClickedListener != null
            }
        }

        override fun setOnSearchClickedListener(listener: OnClickListener?) {
            onSearchClickedListener = listener
            binding.searchOrb.setOnOrbClickedListener(onSearchClickedListener)
        }

        override fun getTitle(): CharSequence? {
            return binding.titleTextView.text
        }

        override fun setTitle(titleText: CharSequence?) {
            binding.titleTextView.text = titleText
            updateTitleViewVisibility()
        }

        private fun updateTitleViewVisibility() {
            binding.titleTextView.isVisible = title.isNullOrEmpty().not()
        }
    }

    override fun getTitleViewAdapter() = titleViewAdapter

    private val binding = ChannelTitleViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var onSearchClickedListener: OnClickListener? = null

    val followUnfollowButton = binding.followUnfollowButton

    fun setChannel(channel: ChannelUiState?) {
        binding.channel = channel
    }

    fun setFollowUnfollowButtonClickListener(listener: OnClickListener) {
        binding.followUnfollowButton.setOnClickListener(listener)
    }
}
