package app.newproj.lbrytv.util

import android.content.Context
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import io.lbry.lbrysdk.Utils
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.inject.Inject

class AuthTokenEncryptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val keyStore: KeyStore
) {
    fun encrypt(planTextToken: String): String {
        val byteArray = planTextToken.toByteArray(StandardCharsets.UTF_8)
        return Utils.encrypt(byteArray, context, keyStore)
    }

    fun decrypt(encryptedToken: String): String {
        return Utils.decrypt(
            Base64.decode(encryptedToken, Base64.NO_WRAP),
            context,
            keyStore
        ).toString(StandardCharsets.UTF_8)
    }
}
