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

package app.newproj.lbrytv.data.repo

import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.SupportAmountOption
import javax.inject.Inject

class SupportAmountRepository @Inject constructor() {
    fun supportAmounts(): List<SupportAmountOption> = listOf(
        SupportAmountOption(
            R.id.amount_option_1_credit,
            R.plurals.credit_amount,
            1,
        ),
        SupportAmountOption(
            R.id.amount_option_5_credits,
            R.plurals.credit_amount,
            5,
        ),
        SupportAmountOption(
            R.id.amount_option_25_credits,
            R.plurals.credit_amount,
            25,
            true
        ),
        SupportAmountOption(
            R.id.amount_option_100_credits,
            R.plurals.credit_amount,
            100,
        ),
        SupportAmountOption(
            R.id.amount_option_500_credits,
            R.plurals.credit_amount,
            500,
        ),
        SupportAmountOption(
            R.id.amount_option_1000_credits,
            R.plurals.credit_amount,
            1000,
        ),
    )
}
