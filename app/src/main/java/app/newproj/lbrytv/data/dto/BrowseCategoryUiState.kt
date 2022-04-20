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

package app.newproj.lbrytv.data.dto

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class BrowseCategoryUiState(
    val id: Int,
    @StringRes val nameRes: Int,
    @DrawableRes val iconRes: Int,
    val items: Flow<PagingData<BrowseItemUiState>>,
) {
    companion object {
        fun fromBrowseCategory(browseCategory: BrowseCategory) = BrowseCategoryUiState(
            browseCategory.id,
            browseCategory.name, browseCategory.icon,
            browseCategory.items.map { pagingData ->
                pagingData.map { browseItem ->
                    when (browseItem) {
                        is Video -> VideoUiState.fromVideo(browseItem)
                        is Channel -> ChannelUiState.fromChannel(browseItem)
                        is Setting -> browseItem
                    }
                }
            }
        )
    }
}

sealed interface BrowseItemUiState {
    val id: String
}

class BrowseItemUiStateComparator<T : BrowseItemUiState> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.id == newItem.id
    }

    // https://googlesamples.github.io/android-custom-lint-rules/checks/DiffUtilEquals.md.html
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}
