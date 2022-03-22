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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import androidx.room.Update
import app.newproj.lbrytv.data.dto.ClaimSearchResult
import app.newproj.lbrytv.data.entity.Claim

private const val ROW_ID_ON_CONFLICT = -1L

@Dao
abstract class ClaimSearchResultDao {
    @Insert(entity = Claim::class, onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(claim: ClaimSearchResult.Item): Long

    @Insert(entity = Claim::class, onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(claims: List<ClaimSearchResult.Item>): List<Long>

    @Update(entity = Claim::class, onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun update(claim: ClaimSearchResult.Item)

    @Update(entity = Claim::class, onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun update(claims: List<ClaimSearchResult.Item>)

    @Transaction
    open suspend fun upsert(item: ClaimSearchResult.Item) {
        if (insert(item) == ROW_ID_ON_CONFLICT) {
            update(item)
        }
    }

    @Transaction
    open suspend fun upsert(items: List<ClaimSearchResult.Item>) {
        insert(items).mapIndexedNotNull { index, rowId ->
            items[index].takeIf { rowId == ROW_ID_ON_CONFLICT }
        }.takeIf { it.isNotEmpty() }?.let {
            update(it)
        }
    }
}
