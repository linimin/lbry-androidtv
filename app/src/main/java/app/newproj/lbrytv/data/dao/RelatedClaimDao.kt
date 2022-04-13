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

import android.app.SearchManager
import android.database.Cursor
import android.provider.BaseColumns
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import app.newproj.lbrytv.data.dto.RelatedClaim
import app.newproj.lbrytv.data.entity.Claim

private const val ROW_ID_ON_CONFLICT = -1L

@Dao
abstract class RelatedClaimDao {
    @Insert(entity = Claim::class, onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(claim: RelatedClaim): Long

    @Insert(entity = Claim::class, onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(claims: List<RelatedClaim>): List<Long>

    @Update(entity = Claim::class, onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun update(claim: RelatedClaim)

    @Update(entity = Claim::class, onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun update(claims: List<RelatedClaim>)

    @Transaction
    open suspend fun upsert(item: RelatedClaim) {
        if (insert(item) == ROW_ID_ON_CONFLICT) {
            update(item)
        }
    }

    @Transaction
    open suspend fun upsert(items: List<RelatedClaim>) {
        insert(items).mapIndexedNotNull { index, rowId ->
            items[index].takeIf { rowId == ROW_ID_ON_CONFLICT }
        }.takeIf { it.isNotEmpty() }?.let {
            update(it)
        }
    }

    @Query("""
        SELECT 
          id AS ${BaseColumns._ID},
          title AS ${SearchManager.SUGGEST_COLUMN_TEXT_1},
          channel_name AS ${SearchManager.SUGGEST_COLUMN_TEXT_2},
          strftime('%Y', release_time) AS ${SearchManager.SUGGEST_COLUMN_PRODUCTION_YEAR},
          (duration * 1000) AS ${SearchManager.SUGGEST_COLUMN_DURATION},
          thumbnail AS ${SearchManager.SUGGEST_COLUMN_RESULT_CARD_IMAGE},
          
          CASE WHEN channel_name IS NOT NUll THEN
            'https://lbrytv.newproj.app/video'
          ELSE
            'https://lbrytv.newproj.app/CHANNEL'
          END AS ${SearchManager.SUGGEST_COLUMN_INTENT_DATA},
          
          id AS ${SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID}
        FROM claim
        INNER JOIN claim_lookup 
        ON claim_lookup.label = 'SEARCH_SUGGESTIONS' AND claim.id = claim_lookup.claim_id 
        ORDER BY claim_lookup.sorting_order ASC
    """)
    abstract fun searchSuggestionsCursor(): Cursor
}
