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

package app.newproj.lbrytv.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import app.newproj.lbrytv.data.repo.HomeScreenChannelsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

class HomeScreenChannelsUpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val homeScreenChannelsRepository: HomeScreenChannelsRepository,
) : CoroutineWorker(appContext, workerParams) {
    @AssistedFactory
    interface Factory {
        fun HomeScreenChannelsUpdateWorker(
            appContext: Context,
            workerParameters: WorkerParameters,
        ): HomeScreenChannelsUpdateWorker
    }

    override suspend fun doWork(): Result {
        return try {
            homeScreenChannelsRepository.synchronize()
            Result.success()
        } catch (error: Throwable) {
            Result.failure()
        }
    }
}

class HomeChannelsUpdateWorkerFactory @Inject constructor(
    private val homeScreenChannelsUpdateWorkerFactory: HomeScreenChannelsUpdateWorker.Factory,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? =
        if (workerClassName == HomeScreenChannelsUpdateWorker::class.java.name) {
            homeScreenChannelsUpdateWorkerFactory.HomeScreenChannelsUpdateWorker(
                appContext,
                workerParameters
            )
        } else {
            null
        }
}
