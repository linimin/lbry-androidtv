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

package app.newproj.lbrytv.ui.search

import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.ObjectAdapter.NO_ID
import androidx.lifecycle.ViewModel
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.ItemComparator
import app.newproj.lbrytv.data.dto.LocalizableHeaderItem
import app.newproj.lbrytv.data.dto.PagingListRow
import app.newproj.lbrytv.ui.presenter.ItemPresenterSelector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel @Inject constructor(
) : ViewModel() {
    private val query = MutableStateFlow<String?>(null)

    val searchResultRow: PagingListRow = PagingListRow(
        NO_ID.toLong(),
        LocalizableHeaderItem(NO_ID.toLong(), null, R.string.search_result),
        PagingDataAdapter(ItemPresenterSelector, ItemComparator()),
        emptyFlow()
    )

    fun search(query: String?) {
        this.query.value = query
    }
}
