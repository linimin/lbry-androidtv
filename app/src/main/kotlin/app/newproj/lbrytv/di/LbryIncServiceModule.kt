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

package app.newproj.lbrytv.di

import app.newproj.lbrytv.service.LbryIncService
import app.newproj.lbrytv.service.LbryIncServiceAuthInterceptor
import app.newproj.lbrytv.service.LbryIncServiceBodyConverterFactory
import app.newproj.lbrytv.service.LbryIncServiceErrorInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LbryIncServiceModule {
    @Provides
    @Singleton
    fun lbryIncService(
        authInterceptor: LbryIncServiceAuthInterceptor,
        gsonConverterFactory: GsonConverterFactory,
        errorInterceptor: LbryIncServiceErrorInterceptor,
    ): LbryIncService {
        val client = OkHttpClient.Builder()
            .protocols(listOf(Protocol.HTTP_1_1))
            .addInterceptor(authInterceptor)
            .addInterceptor(errorInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl("https://api.lbry.com/")
            .client(client)
            .addConverterFactory(LbryIncServiceBodyConverterFactory)
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(LbryIncService::class.java)
    }
}
