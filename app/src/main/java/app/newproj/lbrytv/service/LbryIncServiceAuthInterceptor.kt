package app.newproj.lbrytv.service

import app.newproj.lbrytv.data.repo.UserRepository
import app.newproj.lbrytv.util.AuthTokenEncryptor
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Provider

class LbryIncServiceAuthInterceptor @Inject constructor(
    // Use Provider to break the circular dependency.
    private val userRepository: Provider<UserRepository>,
    private val tokenEncryptor: AuthTokenEncryptor,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return with(chain.request()) {
            val authToken = if (url.encodedPath != "/user/new") authToken() else null
            url.newBuilder()
                .addQueryParameter("auth_token", authToken)
                .build()
                .let { newBuilder().url(it).build() }
        }.let(chain::proceed)
    }

    private fun authToken() = runBlocking {
//        "GW5qoy5JAnhyecR6njFsVJYAtE5R66aU"
        userRepository.get().user().authToken?.let {
            tokenEncryptor.decrypt(it)
        }
    }
}
