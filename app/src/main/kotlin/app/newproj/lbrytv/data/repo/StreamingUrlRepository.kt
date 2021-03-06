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

import android.net.Uri
import androidx.core.net.toUri
import app.newproj.lbrytv.data.dto.DownloadRequest
import app.newproj.lbrytv.data.dto.StreamingUrl
import app.newproj.lbrytv.service.LbrynetService
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class StreamingUrlRepository @Inject constructor(
    private val lbrynetService: LbrynetService,
) {
    suspend fun streamingUrl(claimUri: Uri): StreamingUrl {
        val streamingUrl = lbrynetService.get(DownloadRequest(claimUri)).streamingUrl
        val contentType = suspendCoroutine<String?> {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(streamingUrl)
                .head()
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    it.resumeWith(Result.success(response.header("Content-Type")))
                }

                override fun onFailure(call: Call, e: IOException) {
                    it.resumeWith(Result.failure(e))
                }
            })
        }
        return StreamingUrl(
            streamingUrl.toUri(),
            contentType == "application/x-mpegurl"
        )
    }
}
