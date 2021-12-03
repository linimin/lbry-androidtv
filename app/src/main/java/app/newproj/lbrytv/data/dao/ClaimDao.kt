package app.newproj.lbrytv.data.dao

import androidx.paging.PagingSource
import androidx.room.*
import app.newproj.lbrytv.data.dto.ClaimSearchResult
import app.newproj.lbrytv.data.dto.MyClaimSearchResult
import app.newproj.lbrytv.data.dto.RelatedClaim
import app.newproj.lbrytv.data.dto.ResolvedClaim
import app.newproj.lbrytv.data.entity.Claim
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ClaimDao : BaseDao<Claim>() {
    @Insert(entity = Claim::class, onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertSearchResult(claims: List<ClaimSearchResult.Item>): List<Long>

    @Insert(entity = Claim::class, onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertRelated(claims: List<RelatedClaim>): List<Long>

    @Insert(entity = Claim::class, onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertResolved(claims: List<ResolvedClaim>): List<Long>

    @Insert(entity = Claim::class, onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertMyChannel(claim: List<MyClaimSearchResult.Item>): List<Long>

    @Update(entity = Claim::class)
    abstract suspend fun updateSearchResult(claims: List<ClaimSearchResult.Item>)

    @Update(entity = Claim::class)
    abstract suspend fun updateRelated(claims: List<RelatedClaim>)

    @Update(entity = Claim::class)
    abstract suspend fun updateResolved(claims: List<ResolvedClaim>)

    @Update(entity = Claim::class)
    abstract suspend fun updateMyChannel(claims: List<MyClaimSearchResult.Item>)

    suspend fun insertOrUpdateSearchResult(claims: List<ClaimSearchResult.Item>) {
        insertOrUpdate(claims, ::insertSearchResult, ::updateSearchResult)
    }

    suspend fun insertOrUpdateRelated(claims: List<RelatedClaim>) {
        insertOrUpdate(claims, ::insertRelated, ::updateRelated)
    }

    suspend fun insertOrUpdateResolved(claims: List<ResolvedClaim>) {
        insertOrUpdate(claims, ::insertResolved, ::updateResolved)
    }

    suspend fun insertOrUpdateMyChannel(claims: List<MyClaimSearchResult.Item>) {
        insertOrUpdate(claims, ::insertMyChannel, ::updateMyChannel)
    }

    @Query("SELECT * from claim where channel_id=:channelId ORDER BY release_time DESC")
    abstract fun claimsByChannelId(channelId: String): PagingSource<Int, Claim>

    @Query("SELECT * from claim where id=:id")
    abstract fun claimById(id: String): Flow<Claim>

    @Query("SELECT * FROM claim INNER JOIN subscribed_content ON claim.id = subscribed_content.id ORDER BY release_time DESC")
    abstract fun subscribedContent(): PagingSource<Int, Claim>

    @Query("SELECT * FROM claim INNER JOIN trending_claim ON claim.id = trending_claim.id ORDER BY  trending_group DESC, trending_mixed DESC")
    abstract fun trending(): PagingSource<Int, Claim>

    @Query("SELECT * FROM claim INNER JOIN related_claim ON claim.id = related_claim.id ORDER by cast(effective_amount as real) DESC")
    abstract fun related(): PagingSource<Int, Claim>

    @Query("SELECT * FROM claim INNER JOIN suggested_claim ON claim.id = suggested_claim.id ORDER by cast(effective_amount as real) DESC")
    abstract fun suggestedChannels(): PagingSource<Int, Claim>

    @Query("SELECT * FROM claim INNER JOIN subscription ON claim.id = subscription.claim_id")
    abstract fun subscriptions(): PagingSource<Int, Claim>

    @Query("SELECT * FROM claim INNER JOIN my_channel ON claim.id = my_channel.id")
    abstract fun myChannels(): PagingSource<Int, Claim>

    @Query("DELETE FROM claim")
    abstract suspend fun clear()
}
