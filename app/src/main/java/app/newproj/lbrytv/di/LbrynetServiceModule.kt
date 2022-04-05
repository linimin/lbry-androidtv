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

import app.newproj.lbrytv.auth.LbrynetServiceAuthInterceptor
import app.newproj.lbrytv.service.JsonRpcBodyConverterFactory
import app.newproj.lbrytv.service.JsonRpcBodyFiller
import app.newproj.lbrytv.service.JsonRpcInterceptor
import app.newproj.lbrytv.service.LbrynetProxyServiceInterceptor
import app.newproj.lbrytv.service.LbrynetService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LbrynetServiceModule {
    @Provides
    @Singleton
    fun lbrynetService(
        jsonRpcBodyFiller: JsonRpcBodyFiller,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        gsonConverterFactory: GsonConverterFactory,
        authInterceptor: LbrynetServiceAuthInterceptor,
        jsonRpcInterceptor: JsonRpcInterceptor,
    ): LbrynetService {
        val client = OkHttpClient.Builder()
            .addInterceptor(jsonRpcInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(LbrynetProxyServiceInterceptor)
            .addInterceptor(jsonRpcBodyFiller)
            .build()
        return Retrofit.Builder()
            .baseUrl("https://api.na-backend.odysee.com/")
            .client(client)
            .addConverterFactory(JsonRpcBodyConverterFactory)
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(LbrynetService::class.java)
    }
}
