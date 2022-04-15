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

package app.newproj.lbrytv.data.repo

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import androidx.tvprovider.media.tv.Channel
import androidx.tvprovider.media.tv.ChannelLogoUtils
import androidx.tvprovider.media.tv.PreviewChannelHelper
import androidx.tvprovider.media.tv.PreviewProgram
import androidx.tvprovider.media.tv.TvContractCompat
import app.newproj.lbrytv.R
import app.newproj.lbrytv.di.IODispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomeScreenChannelsRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val videosRepository: VideosRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun synchronize() = withContext(ioDispatcher) {
        val defaultChannel = PreviewChannelHelper(appContext).allChannels.firstOrNull()
        val updatedChannel = Channel.Builder()
            .setType(TvContractCompat.Channels.TYPE_PREVIEW)
            .setInternalProviderId("RECOMMENDED_CHANNEL")
            .setDisplayName("Channel Name")
            .setAppLinkIntentUri(Uri.parse("https://lbrytv.newproj.app/"))
            .setDisplayName(appContext.getString(R.string.odysee_featured))
            .setDescription("Featured channel description")
            .build()
        val channelId = if (defaultChannel != null) {
            val channelPrograms =
                TvContractCompat.buildPreviewProgramsUriForChannel(defaultChannel.id)
            appContext.contentResolver.delete(channelPrograms, null, null)
            appContext.contentResolver.update(
                TvContractCompat.buildChannelUri(defaultChannel.id),
                updatedChannel.toContentValues(),
                null,
                null
            )
            defaultChannel.id
        } else {
            val channelUri = appContext.contentResolver.insert(
                TvContractCompat.Channels.CONTENT_URI, updatedChannel.toContentValues())
            ContentUris.parseId(channelUri!!)
        }
        ChannelLogoUtils.storeChannelLogo(appContext, channelId, resourceUri(R.mipmap.channel))
        TvContractCompat.requestChannelBrowsable(appContext, channelId)
        videosRepository.recommendedVideos().forEach { video ->
            @SuppressLint("RestrictedApi")
            // https://issuetracker.google.com/issues/225186479
            val program = PreviewProgram.Builder()
                .setChannelId(channelId)
                .setInternalProviderId(video.id)
                .setType(TvContractCompat.PreviewPrograms.TYPE_CLIP)
                .setTitle(video.claim.title)
                .setDurationMillis(video.claim.duration?.toInt()?.let { it * 1000 } ?: 0)
                .setIntentUri(Uri.parse("https://lbrytv.newproj.app/video/${video.id}"))
                .setPosterArtUri(video.claim.thumbnail)
                .setPosterArtAspectRatio(TvContractCompat.PreviewPrograms.ASPECT_RATIO_16_9)
                .setDescription(video.claim.description)
                .build()
            PreviewChannelHelper(appContext).publishPreviewProgram(program)
        }
    }

    private fun resourceUri(id: Int) = appContext.resources.let {
        Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(it.getResourcePackageName(id))
            .appendPath(it.getResourceTypeName(id))
            .appendPath(it.getResourceEntryName(id))
            .build()
    }
}
