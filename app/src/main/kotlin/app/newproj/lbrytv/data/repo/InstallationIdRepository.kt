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

import androidx.datastore.core.DataStore
import app.newproj.lbrytv.data.AppData
import kotlinx.coroutines.flow.first
import org.bitcoinj.core.Base58
import java.security.MessageDigest
import java.util.Random
import javax.inject.Inject

class InstallationIdRepository @Inject constructor(
    private val appDataStore: DataStore<AppData>,
) {
    suspend fun installationId(): String {
        return appDataStore.data.first()
            .installationId.takeIf { it.isNotEmpty() }
            ?: newInstallationId().also { newInstallationId ->
                appDataStore.updateData {
                    it.toBuilder()
                        .setInstallationId(newInstallationId)
                        .build()
                }
            }
    }

    private fun newInstallationId(): String {
        val byteArray = ByteArray(64)
        Random().nextBytes(byteArray)
        val messageDigest = MessageDigest.getInstance("SHA-384")
        val hash = messageDigest.digest(byteArray)
        return hash.base58Encoded()
    }
}

private fun ByteArray.base58Encoded(): String = Base58.encode(this)
