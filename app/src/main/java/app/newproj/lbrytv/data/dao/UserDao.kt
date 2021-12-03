package app.newproj.lbrytv.data.dao

import androidx.room.*
import app.newproj.lbrytv.data.dto.TokenUser
import app.newproj.lbrytv.data.dto.NewUser
import app.newproj.lbrytv.data.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDao : BaseDao<User>() {
    @Insert(entity = User::class, onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertNewUser(newUser: NewUser): Long

    @Update(entity = User::class)
    abstract suspend fun updateNewUser(newUser: NewUser)

    suspend fun insertOrUpdateNewUser(newUser: NewUser) {
        insertOrUpdate(newUser, ::insertNewUser, ::updateNewUser)
    }

    @Insert(entity = User::class, onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertAuthorizedUser(user: TokenUser): Long

    @Update(entity = User::class)
    abstract suspend fun updateAuthorizedUser(user: TokenUser)

    suspend fun insertOrUpdateAuthorizedUser(user: TokenUser) {
        insertOrUpdate(user, ::insertAuthorizedUser, ::updateAuthorizedUser)
    }

    @Query("SELECT * FROM user LIMIT 1")
    abstract suspend fun user(): User?

    @Query("SELECT * FROM user LIMIT 1")
    abstract fun userFlow(): Flow<User?>
   
    @Query("DELETE from user")
    abstract suspend fun clear()
}
