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

package app.newproj.lbrytv.data.datasource

import android.util.Size
import androidx.datastore.core.DataStore
import androidx.preference.PreferenceDataStore
import app.newproj.lbrytv.data.AppData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

val AUTO_VIDEO_SIZE = Size(Int.MAX_VALUE, Int.MAX_VALUE)

class PlayerSettingsDataSource @Inject constructor(
    private val appDataStore: DataStore<AppData>,
) : PreferenceDataStore() {
    fun preferredVideoSize(): Flow<Size> = appDataStore.data.map {
        Size.parseSize(it.preferredVideoSize)
    }.catch { AUTO_VIDEO_SIZE }

    override fun putString(key: String, value: String?) {
        if (key == KEY_PREFERRED_VIDEO_SIZE) {
            runBlocking {
                appDataStore.updateData {
                    it.toBuilder()
                        .setPreferredVideoSize(value)
                        .build()
                }
            }
        }
    }

    override fun getString(key: String, defValue: String?): String? =
        if (key == KEY_PREFERRED_VIDEO_SIZE) {
            runBlocking {
                appDataStore.data.first()
                    .preferredVideoSize
                    .takeIf { it.isNotEmpty() }
                    ?: AUTO_VIDEO_SIZE.toString()
            }
        } else {
            null
        }

    companion object {
        const val KEY_PREFERRED_VIDEO_SIZE = "KEY_PREFERRED_VIDEO_SIZE"
    }
}
