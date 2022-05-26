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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowHeaderPresenter
import app.newproj.lbrytv.data.dto.HasLocalizableHeader
import app.newproj.lbrytv.databinding.TextRowHeaderBinding

class LocalizableRowHeaderPresenter : RowHeaderPresenter() {
    class ViewHolder(
        val binding: TextRowHeaderBinding
    ) : RowHeaderPresenter.ViewHolder(binding.root as View)

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TextRowHeaderBinding.inflate(layoutInflater, parent, false)
        val viewHolder = ViewHolder(binding)
        setSelectLevel(viewHolder, 0f)
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        require(viewHolder is ViewHolder)
        require(item is HasLocalizableHeader)
        val context = viewHolder.view.context
        val headerName = context.getString(item.localizableHeader.nameRes)
        viewHolder.binding.rowHeader.text = headerName
    }
}
