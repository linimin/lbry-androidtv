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
import app.newproj.lbrytv.data.dto.LbryUri
import app.newproj.lbrytv.data.dto.UserPreference
import app.newproj.lbrytv.data.dto.UserPreferenceUpdateRequest
import app.newproj.lbrytv.service.LbrynetService
import javax.inject.Inject

class UserPreferenceRepository @Inject constructor(
    private val lbrynetService: LbrynetService,
) {
    suspend fun addSubscription(uri: Uri) {
        val preference = lbrynetService.preference()
        val shared = preference.shared
        val value = shared?.value
        val subscriptions = value?.subscriptions?.toMutableList() ?: mutableListOf()
        val followings = value?.following?.toMutableList() ?: mutableListOf()
        if (subscriptions.contains(uri.toString()).not()) {
            subscriptions.add(uri.toString())
        }
        if (followings.find { it.uri == uri } == null) {
            followings.add(UserPreference.Shared.Value.Following(false, uri))
        }
        lbrynetService.setPreference(
            UserPreferenceUpdateRequest(
                "shared",
                shared?.copy(
                    value = value?.copy(
                        subscriptions = subscriptions,
                        following = followings
                    )
                )
            )
        )
    }

    suspend fun removeSubscription(uri: Uri) {
        val preference = lbrynetService.preference()
        val shared = preference.shared
        val value = shared?.value
        val subscriptions = value?.subscriptions?.toMutableList() ?: mutableListOf()
        val followings = value?.following?.toMutableList() ?: mutableListOf()
        subscriptions.removeIf { LbryUri.normalize(it) == LbryUri.normalize(uri.toString()) }
        followings.removeIf { LbryUri.normalize(it.uri.toString()) == LbryUri.normalize(uri.toString()) }
        lbrynetService.setPreference(
            UserPreferenceUpdateRequest(
                "shared",
                shared?.copy(
                    value = value?.copy(
                        subscriptions = subscriptions,
                        following = followings
                    )
                )
            )
        )
    }
}
