package app.newproj.lbrytv.data.dao

import androidx.room.*
import app.newproj.lbrytv.data.entity.Subscription
import kotlinx.coroutines.flow.Flow

typealias SubscriptionDto = app.newproj.lbrytv.data.dto.Subscription

@Dao
abstract class SubscriptionDao : BaseDao<Subscription>() {
    @Insert(entity = Subscription::class, onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertSubscriptions(subscriptions: List<SubscriptionDto>): List<Long>

    @Update(entity = Subscription::class)
    abstract suspend fun updateSubscriptions(subscriptions: List<SubscriptionDto>)

    suspend fun insertOrUpdateSubscriptions(subscriptions: List<SubscriptionDto>) {
        insertOrUpdate(subscriptions, ::insertSubscriptions, ::updateSubscriptions)
    }

    @Query("SELECT * FROM subscription")
    abstract fun subscriptionsFlow(): Flow<List<Subscription>>

    @Query("SELECT * FROM subscription WHERE claim_id = :claimId")
    abstract fun subscriptionById(claimId: String): Subscription?

    @Query("SELECT * FROM subscription WHERE claim_id = :claimId")
    abstract fun subscriptionFlowById(claimId: String): Flow<Subscription?>

    @Query("DELETE FROM subscription")
    abstract suspend fun clear()

    @Query("DELETE FROM subscription WHERE claim_id = :claimId")
    abstract suspend fun deleteByClaimId(claimId: String)
}
