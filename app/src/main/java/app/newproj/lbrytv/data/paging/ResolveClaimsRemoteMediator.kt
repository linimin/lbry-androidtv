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

package app.newproj.lbrytv.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.room.withTransaction
import app.newproj.lbrytv.data.AppDatabase
import app.newproj.lbrytv.data.dto.ClaimResolveRequest
import app.newproj.lbrytv.data.entity.Claim
import app.newproj.lbrytv.data.entity.ClaimLookup
import app.newproj.lbrytv.data.entity.RemoteKey
import app.newproj.lbrytv.service.ApiException
import app.newproj.lbrytv.service.LbrynetService
import app.newproj.lbrytv.service.NoDataApiException
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_SORTING_ORDER = 1

@OptIn(ExperimentalPagingApi::class)
abstract class ResolveClaimsRemoteMediator(
    private val lbrynetService: LbrynetService,
    private val db: AppDatabase,
) : BaseRemoteMediator<ClaimResolveRequest, Claim>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Claim>): MediatorResult {
        try {
            val remoteKeyLabel = label
            var nextSortingOrder = when (loadType) {
                LoadType.REFRESH -> STARTING_SORTING_ORDER
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = db.remoteKeyDao().remoteKey(remoteKeyLabel)
                    remoteKey?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    remoteKey.nextSortingOrder
                }
            }
            val request = onCreateInitialRequest()
            val claims = request?.let { lbrynetService.resolveClaims(request).values.toList() }
                ?: emptyList()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeyDao().delete(remoteKeyLabel)
                    db.claimLookupDao().deleteAll(remoteKeyLabel)
                }
                db.resolvedClaimDao().insert(claims)
                val claimLookups = claims.map {
                    ClaimLookup(remoteKeyLabel, it.claimId, nextSortingOrder++)
                }
                db.claimLookupDao().insert(claimLookups)
                val remoteKey = RemoteKey(remoteKeyLabel, null, nextSortingOrder)
                db.remoteKeyDao().upsert(remoteKey)
            }
            return MediatorResult.Success(endOfPaginationReached = claims.isEmpty())
        } catch (e: ApiException) {
            return MediatorResult.Error(e)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        } catch (e: NoDataApiException) {
            return MediatorResult.Error(e)
        }
    }
}
