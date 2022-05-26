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

import android.net.Uri
import app.newproj.lbrytv.data.dto.ClaimSearchRequest
import app.newproj.lbrytv.data.dto.ClaimSearchResult
import app.newproj.lbrytv.data.repo.UserPreferenceRepository
import app.newproj.lbrytv.service.LbrynetService
import javax.inject.Inject

class ChannelRemoteDataSource @Inject constructor(
    private val lbrynetService: LbrynetService,
    private val userPreferenceRepo: UserPreferenceRepository,
) {
    suspend fun channel(id: String): ClaimSearchResult.Item? =
        lbrynetService.searchClaims(ClaimSearchRequest(claimId = id))
            .items?.firstOrNull()

    suspend fun follow(permanentUrl: Uri) {
        userPreferenceRepo.addSubscription(permanentUrl)
    }

    suspend fun unfollow(permanentUrl: Uri) {
        userPreferenceRepo.removeSubscription(permanentUrl)
    }
}
