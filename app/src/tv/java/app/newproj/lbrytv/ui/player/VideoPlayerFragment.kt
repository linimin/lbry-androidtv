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

package app.newproj.lbrytv.ui.player

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.leanback.LeanbackPlayerAdapter
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import app.newproj.lbrytv.NavGraphDirections
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.StreamingUrl
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

private const val PLAYER_CONTROL_UPDATE_PERIOD_MILLIS = 100
private const val KEY_LAST_PLAYBACK_POSITION = "KEY_LAST_PLAYBACK_POSITION"

/**
 * References:
 * * [Introducing Jetpack Media3](https://android-developers.googleblog.com/2021/10/jetpack-media3.html)
 */
@AndroidEntryPoint
class VideoPlayerFragment : VideoSupportFragment() {
    private val viewModel: VideoPlayerViewModel by viewModels()
    private val navController by lazy { findNavController() }
    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private lateinit var playbackGlue: PlaybackTransportControlGlue<LeanbackPlayerAdapter>
    private var lastPlaybackPosition: Long? = null
    @Inject lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            lastPlaybackPosition = it.getLong(KEY_LAST_PLAYBACK_POSITION)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(Color.BLACK)
        surfaceView.keepScreenOn = true
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                playbackGlue.apply {
                    title = it.title
                    subtitle = it.subtitle
                }
                it.streamingUrl?.let(::play)
                it.errorMessage?.let(::goToErrorScreen)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        lastPlaybackPosition = player.currentPosition
        releasePlayer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lastPlaybackPosition?.let {
            outState.putLong(KEY_LAST_PLAYBACK_POSITION, it)
        }
    }

    override fun onError(errorCode: Int, errorMessage: CharSequence?) {
        // FIXME: This is a quick fix to give the user an opportunity to close the player screen.
        viewLifecycleOwner.lifecycleScope.launch {
            delay(3.seconds)
            if (navController.currentDestination?.id != R.id.errorFragment) {
                goToErrorScreen(errorMessage?.toString())
            }
        }
        firebaseAnalytics.logEvent(
            "video_play_error",
            bundleOf(
                "claim_name" to viewModel.uiState.value.claimName,
                "claim_id" to viewModel.uiState.value.claimId,
                "error_code" to errorCode,
                "error_message" to errorMessage,
            )
        )
    }

    private fun initializePlayer() {
        val context = requireContext()
        player = ExoPlayer.Builder(context).build()
        mediaSession = MediaSession.Builder(context, player).build()
        val playerAdapter = LeanbackPlayerAdapter(
            context,
            player,
            PLAYER_CONTROL_UPDATE_PERIOD_MILLIS
        )
        playbackGlue = CustomPlaybackTransportControlGlue(context, playerAdapter) {
            when (it.id) {
                R.id.guided_action_support.toLong() -> goToSupportScreen()
                R.id.guided_action_channel.toLong() ->
                    viewModel.uiState.value.channelId?.let(::goToChannelScreen)
            }
        }.apply {
            host = VideoSupportFragmentGlueHost(this@VideoPlayerFragment)
            isSeekEnabled = true
        }
        adapter = ArrayObjectAdapter(playbackGlue.playbackRowPresenter).apply {
            add(playbackGlue.controlsRow)
        }
    }

    private fun releasePlayer() {
        mediaSession.release()
        // Release the player after releasing the session.
        player.release()
    }

    private fun play(streamUrl: StreamingUrl) {
        val mediaItem = MediaItem.Builder()
            .setUri(streamUrl.url)
            .apply {
                if (streamUrl.videoIsTranscoded) {
                    setMimeType(MimeTypes.APPLICATION_M3U8)
                }
            }
            .build()
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
        lastPlaybackPosition?.let { player.seekTo(it) }
    }

    private fun goToChannelScreen(channelId: String) {
        navController.navigate(
            VideoPlayerFragmentDirections
                .actionVideoPlayerFragmentToChannelVideosFragment(channelId),
            NavOptions.Builder()
                .setPopUpTo(R.id.channelVideosFragment, true)
                .build()
        )
    }

    private fun goToSupportScreen() {
        viewModel.uiState.value.channelId?.let {
            navController.navigate(
                VideoPlayerFragmentDirections.actionVideoPlayerFragmentToSupportFragment(it)
            )
        }
    }

    private fun goToErrorScreen(message: String?) {
        navController.navigate(NavGraphDirections.actionGlobalErrorFragment(message))
        viewModel.errorMessageShown()
    }
}

private class CustomPlaybackTransportControlGlue(
    context: Context,
    playerAdapter: LeanbackPlayerAdapter,
    private val onActionClicked: (action: Action) -> Unit,
) : PlaybackTransportControlGlue<LeanbackPlayerAdapter>(context, playerAdapter) {
    override fun onCreatePrimaryActions(primaryActionsAdapter: ArrayObjectAdapter) {
        super.onCreatePrimaryActions(primaryActionsAdapter)
        primaryActionsAdapter.add(
            Action(
                R.id.guided_action_channel.toLong(),
                null, null,
                ContextCompat.getDrawable(context, R.drawable.subscriptions)
            )
        )
        primaryActionsAdapter.add(
            Action(
                R.id.guided_action_support.toLong(),
                null, null,
                ContextCompat.getDrawable(context, R.drawable.lbc)
            )
        )
    }

    override fun onActionClicked(action: Action) {
        super.onActionClicked(action)
        onActionClicked.invoke(action)
    }
}
