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

package app.newproj.lbrytv.usecase

import app.newproj.lbrytv.data.repo.AccountsRepository
import app.newproj.lbrytv.data.repo.ChannelsRepository
import app.newproj.lbrytv.data.repo.SubscriptionsRepository
import app.newproj.lbrytv.di.ApplicationCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class FollowUnfollowChannelUseCase @Inject constructor(
    private val accountsRepository: AccountsRepository,
    private val channelsRepository: ChannelsRepository,
    private val subscriptionsRepository: SubscriptionsRepository,
    @ApplicationCoroutineScope private val externalCoroutineScope: CoroutineScope,
) {
    suspend operator fun invoke(channelId: String) {
        externalCoroutineScope.launch {
            val account = accountsRepository.currentAccount().first() ?: return@launch
            val channel = channelsRepository.channel(channelId).first()
            if (channel.isFollowing) {
                subscriptionsRepository.unfollow(channel, account.name)
            } else {
                subscriptionsRepository.follow(channel, account.name)
            }
        }.join()
    }
}
