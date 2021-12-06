package app.newproj.lbrytv.data.repo

import android.content.Context
import app.newproj.lbrytv.util.Base58
import dagger.hilt.android.qualifiers.ApplicationContext
import io.lbry.lbrysdk.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.*
import javax.inject.Inject
import kotlin.io.path.Path

class InstallationIdRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun installationId(): String = withContext(Dispatchers.IO) {
        val lbrynetDir = Path(Utils.getAppInternalStorageDir(context), "lbrynet").toFile()
        if (lbrynetDir.isDirectory.not()) {
            lbrynetDir.mkdir()
        }
        val idFile = lbrynetDir.resolve("install_id")
        val currentInstallationId = idFile.takeIf { it.exists() }
            ?.readText()?.takeIf { it.isNotEmpty() }
        currentInstallationId ?: newInstallationId().also { idFile.writeText(it) }
    }
}

private fun newInstallationId(): String {
    val byteArray = ByteArray(64)
    Random().nextBytes(byteArray)
    val messageDigest = MessageDigest.getInstance("SHA-384")
    val hash = messageDigest.digest(byteArray)
    return Base58.encode(hash)
}
