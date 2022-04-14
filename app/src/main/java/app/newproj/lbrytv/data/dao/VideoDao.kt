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
import app.newproj.lbrytv.data.dto.Video
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM claim WHERE id = :id")
    fun video(id: String): Flow<Video>

    @Query(
        """
        SELECT * FROM claim
        INNER JOIN claim_lookup 
        ON claim_lookup.label = 'FEATURED_VIDEOS' 
        AND claim.id = claim_lookup.claim_id 
        ORDER BY claim_lookup.sorting_order ASC
        """
    )
    fun featuredVideosPagingSource(): PagingSource<Int, Video>

    @Query(
        """
        SELECT * FROM claim
        INNER JOIN claim_lookup 
        ON claim_lookup.label = 'RECOMMENDED_VIDEOS' 
        AND claim.id = claim_lookup.claim_id 
        ORDER BY claim_lookup.sorting_order ASC
        """
    )
    suspend fun recommendedVideos(): List<Video>

    @Query(
        """
        SELECT * FROM claim 
        INNER JOIN claim_lookup 
        ON claim_lookup.label = :channelId 
        AND claim.id = claim_lookup.claim_id 
        ORDER BY claim_lookup.sorting_order ASC
        """
    )
    fun videos(channelId: String): PagingSource<Int, Video>

    @Query(
        """
        SELECT * FROM claim 
        INNER JOIN claim_lookup 
        ON claim_lookup.label = 'SUBSCRIPTION_VIDEOS' 
        AND claim.id = claim_lookup.claim_id 
        ORDER BY claim_lookup.sorting_order ASC
        """
    )
    fun subscriptionVideos(): PagingSource<Int, Video>
}
