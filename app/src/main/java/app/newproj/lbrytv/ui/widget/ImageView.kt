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

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.databinding.BindingAdapter
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@BindingAdapter("imageUri")
fun setImageUri(imageView: ImageView, uri: Uri?) {
    imageView.load(uri)
}

@BindingAdapter("qrCodeText", "qrCodeSize")
fun setQrCodeText(imageView: ImageView, text: String?, @AttrRes qrCodeSizeAttribute: Int) {
    val typedValue = TypedValue()
    imageView.context.theme.resolveAttribute(qrCodeSizeAttribute, typedValue, true)
    val qrCodeSize = imageView.resources.getDimensionPixelSize(typedValue.resourceId)
    imageView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
        imageView.setImageBitmap(text?.generateQrCode(size = qrCodeSize))
    }
}

private suspend fun String.generateQrCode(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    size: Int = 512,
): Bitmap = withContext(dispatcher) {
    val bitMatrix = QRCodeWriter().encode(
        this@generateQrCode,
        BarcodeFormat.QR_CODE,
        size, size
    )
    Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).apply {
        for (x in 0 until width) {
            for (y in 0 until height) {
                setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
    }
}
