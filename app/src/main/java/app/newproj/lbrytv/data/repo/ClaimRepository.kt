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

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import app.newproj.lbrytv.data.AppDatabase
import app.newproj.lbrytv.data.dto.ClaimLookupLabel
import app.newproj.lbrytv.data.dto.ClaimResolveRequest
import app.newproj.lbrytv.data.dto.ClaimSearchRequest
import app.newproj.lbrytv.data.dto.Video
import app.newproj.lbrytv.data.entity.Claim
import app.newproj.lbrytv.data.paging.ClaimResolveRemoteMediator
import app.newproj.lbrytv.data.paging.ClaimSearchRemoteMediator
import app.newproj.lbrytv.data.paging.RelatedClaimsRemoteMediator
import app.newproj.lbrytv.di.LargePageSize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class ClaimRepository @Inject constructor(
    private val db: AppDatabase,
    private val claimSearchMediatorFactory: ClaimSearchRemoteMediator.Factory,
    private val claimResolveMediatorFactory: ClaimResolveRemoteMediator.Factory,
    private val relatedClaimsMediatorFactory: RelatedClaimsRemoteMediator.Factory,
    @LargePageSize private val pagingConfig: PagingConfig,
) {
    fun claim(id: String): Flow<Claim> = db.claimDao().claim(id)

    fun query(query: String?): Flow<PagingData<Video>> = Pager(
        config = pagingConfig,
        remoteMediator = relatedClaimsMediatorFactory.RelatedClaimsRemoteMediator(
            ClaimLookupLabel.SEARCH_RESULT.name,
            query
        ),
        pagingSourceFactory = {
            db.claimDao().claimsAscendingSorted(ClaimLookupLabel.SEARCH_RESULT.name)
        }
    ).flow.map { pagingData -> pagingData.map { Video(it) } }

    fun claims(label: String, request: ClaimSearchRequest): Flow<PagingData<Claim>> = Pager(
        config = pagingConfig,
        remoteMediator = claimSearchMediatorFactory.ClaimSearchRemoteMediator(label, request),
        pagingSourceFactory = {
            db.claimDao().claimsAscendingSorted(label)
        }
    ).flow

    fun claims(label: String, request: ClaimResolveRequest): Flow<PagingData<Claim>> =
        Pager(
            config = pagingConfig,
            remoteMediator = claimResolveMediatorFactory.ClaimResolveRemoteMediator(label, request),
            pagingSourceFactory = {
                db.claimDao().claimsAscendingSorted(label)
            }
        ).flow
}
