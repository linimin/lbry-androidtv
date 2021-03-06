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

package app.newproj.lbrytv.ui.widget

import android.text.format.DateUtils
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.perfomer.blitz.setTimeAgo
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.Instant

@BindingAdapter("drawableStartCompat")
fun setDrawableStart(textView: TextView, @DrawableRes drawableRes: Int) {
    val drawable = ContextCompat.getDrawable(textView.context, drawableRes)?.apply {
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
    }
    textView.setCompoundDrawables(drawable, null, null, null)
}

private val currencyFormat = DecimalFormat("#,###.########")

@BindingAdapter("lbc")
fun setLbc(textView: TextView, balance: BigDecimal?) {
    textView.text = balance?.let { currencyFormat.format(it) }
}

@BindingAdapter("relativeTime")
fun setRelativeTime(textView: TextView, time: Instant?) {
    time?.let { textView.setTimeAgo(it.toEpochMilli()) }
}

@BindingAdapter("duration")
fun setDuration(textView: TextView, duration: Long?) {
    textView.text = duration?.let { DateUtils.formatElapsedTime(it) }
}
