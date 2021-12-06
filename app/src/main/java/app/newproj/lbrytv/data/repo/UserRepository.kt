package app.newproj.lbrytv.data.repo

import androidx.room.withTransaction
import app.newproj.lbrytv.data.MainDatabase
import app.newproj.lbrytv.data.dto.ApplyWalletSyncRequest
import app.newproj.lbrytv.data.dto.TokenUser
import app.newproj.lbrytv.data.entity.Tag
import app.newproj.lbrytv.data.entity.User
import app.newproj.lbrytv.hiltmodule.LbrynetServiceInitJobScope
import app.newproj.lbrytv.hiltmodule.LbrynetServiceScope
import app.newproj.lbrytv.service.LbryIncService
import app.newproj.lbrytv.service.LbrynetService
import app.newproj.lbrytv.util.AuthTokenEncryptor
import kotlinx.coroutines.Job
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
    @LbrynetServiceInitJobScope private val lbrynetServiceInitJob: Job,
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
        val user = db.withTransaction {
            val authToken = userDao.user()?.authToken
            userDao.clear()
            userDao.insertAuthorizedUser(tokenUser!!)
            val user = userDao.user()!!
            userDao.update(user.copy(authToken = authToken))
            userDao.user()!!
        }
        syncWallet()
        loadPreference()
        return user
    }

    private suspend fun syncWallet() {
        lbrynetServiceInitJob.join()
        val hash = lbrynetService.walletSyncHash()
        val walletSyncData = lbryIncService.walletSyncData(hash)
        val applyWalletSyncRequest = ApplyWalletSyncRequest(
            password = "",
            data = walletSyncData.data,
            blocking = true
        )
        val syncApplyResult = lbrynetService.applyWalletSyncData(applyWalletSyncRequest)
        lbryIncService.setWalletSyncData(
            oldHash = walletSyncData.hash,
            newHash = syncApplyResult.hash,
            data = syncApplyResult.data
        )
    }

    private suspend fun loadPreference() {
        val preference = lbrynetService.preference()
        val tags = preference.shared?.value?.tags?.filterNotNull() ?: emptyList()
        db.withTransaction {
            val tagEntities = tags.map { Tag(it, true) }
            db.tagDao().insertOrUpdate(tagEntities)
        }
    }

    suspend fun deleteUser() {
        db.withTransaction {
            userDao.clear()
            db.tagDao().clear()
        }
    }
}
