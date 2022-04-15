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

package app.newproj.lbrytv

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.tvprovider.media.tv.TvContractCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import app.newproj.lbrytv.worker.HomeScreenChannelsUpdateWorker

/*
 * This [BroadcastReceiver] is invoked when the home screen launcher sends an explicit broadcast
 * to our app. This happens when the user interacts with our programs or after our app is installed
 * and the launcher is ready for our default channel to be created.
 *
 * The app install broadcast is only fired when the app is installed from the Play Store. To test
 * this during development, you can run:
 * $ adb shell am broadcast -a android.media.tv.action.INITIALIZE_PROGRAMS -n app.newproj.lbrytv/.TvLauncherReceiver
 */
class TvLauncherReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            TvContractCompat.ACTION_INITIALIZE_PROGRAMS ->
                WorkManager.getInstance(context)
                    .enqueue(OneTimeWorkRequestBuilder<HomeScreenChannelsUpdateWorker>().build())

            TvContractCompat.ACTION_PREVIEW_PROGRAM_ADDED_TO_WATCH_NEXT -> Unit
            TvContractCompat.ACTION_WATCH_NEXT_PROGRAM_BROWSABLE_DISABLED -> Unit
            TvContractCompat.ACTION_PREVIEW_PROGRAM_BROWSABLE_DISABLED -> Unit
        }
    }
}
