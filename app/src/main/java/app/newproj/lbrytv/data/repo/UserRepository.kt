package app.newproj.lbrytv.data.repo

import androidx.room.withTransaction
import app.newproj.lbrytv.data.MainDatabase
import app.newproj.lbrytv.data.dto.TokenUser
import app.newproj.lbrytv.data.entity.User
import app.newproj.lbrytv.hiltmodule.LbrynetServiceScope
import app.newproj.lbrytv.service.LbryIncService
import app.newproj.lbrytv.service.LbrynetService
import app.newproj.lbrytv.util.AuthTokenEncryptor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import retrofit2.HttpException
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val db: MainDatabase,
    private val installationIdRepo: InstallationIdRepository,
    private val tokenEncryptor: AuthTokenEncryptor,
    private val lbryIncService: LbryIncService,
    @LbrynetServiceScope private val lbrynetService: LbrynetService,
) {
    private val userDao = db.userDao()

    suspend fun user(): User {
        return userDao.user() ?: db.withTransaction {
            val installationId = installationIdRepo.installationId()
            val newUser = lbryIncService.newUser("en", installationId)
            val encryptedAuthToken = newUser.plainTextAuthToken?.let { tokenEncryptor.encrypt(it) }
            userDao.clear()
            userDao.insertNewUser(newUser)
            val user = userDao.user()!!
            userDao.update(user.copy(authToken = encryptedAuthToken))
            userDao.user()!!
        }
    }

    suspend fun user(email: String): User {
        try {
            lbryIncService.addEmail(email, true)
        } catch (e: HttpException) {
            if (e.code() == 409) { // This email is already in use, verify the email.
                lbryIncService.verifyEmail(email, true)
            } else {
                throw e
            }
        }
        var tokenUser: TokenUser? = null
        coroutineScope {
            while (isActive) {
                tokenUser = lbryIncService.tokenUser()
                if (tokenUser?.hasVerifiedEmail == true) {
                    return@coroutineScope
                }
            }
        }
        return db.withTransaction {
            val authToken = userDao.user()?.authToken
            userDao.clear()
            userDao.insertAuthorizedUser(tokenUser!!)
            val user = userDao.user()!!
            userDao.update(user.copy(authToken = authToken))
            userDao.user()!!
        }
    }

    suspend fun deleteUser() {
        userDao.clear()
    }
}
