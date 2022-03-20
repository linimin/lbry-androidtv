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

package app.newproj.lbrytv.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import app.newproj.lbrytv.data.entity.Claim
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ClaimDao : BaseDao<Claim>() {
    @Query("SELECT * from claim where id = :id")
    abstract fun claim(id: String): Flow<Claim>

    @Query(
        """
        SELECT * FROM claim 
        INNER JOIN claim_lookup 
        ON claim_lookup.label = :label 
        AND claim.id = claim_lookup.claim_id 
        ORDER BY claim_lookup.sorting_order ASC
        """
    )
    abstract fun claimsAscendingSorted(label: String): PagingSource<Int, Claim>

    @Query(
        """
        SELECT * FROM claim 
        INNER JOIN claim_lookup 
        ON claim_lookup.label = :label 
        AND claim.id = claim_lookup.claim_id 
        ORDER BY claim_lookup.sorting_order DESC
        """
    )
    abstract fun claimsDescendingSorted(label: String): PagingSource<Int, Claim>

    @Query("SELECT * FROM claim WHERE id IN (:ids)")
    abstract suspend fun claims(ids: List<String>): List<Claim>

    @Query("DELETE FROM claim")
    abstract suspend fun clear()
}
