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
import android.util.Size
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverrides
import androidx.media3.common.TracksInfo
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.upstream.DefaultLoadErrorHandlingPolicy
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
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

private const val PLAYER_CONTROL_UPDATE_PERIOD_MILLIS = 100
private const val KEY_LAST_PLAYBACK_POSITION = "KEY_LAST_PLAYBACK_POSITION"
private const val BUFFER_FOR_PLAYBACK_MS = 1500
private const val MIN_BUFFER_MS = 120_000
private const val MAX_BUFFER_MS = 120_000

/**
 * References:
 * * [Introducing Jetpack Media3](https://android-developers.googleblog.com/2021/10/jetpack-media3.html)
 * * [Use Leanback transport controls](https://developer.android.com/training/tv/playback/transport-controls)
 */
@AndroidEntryPoint
class VideoPlayerFragment : VideoSupportFragment() {
    private val viewModel: VideoPlayerViewModel by viewModels()
    private val navController by lazy { findNavController() }
    @Inject lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private lateinit var playbackGlue: PlaybackTransportControlGlue<LeanbackPlayerAdapter>
    private lateinit var channelAction: Action
    private lateinit var settingsAction: Action
    private lateinit var supportAction: Action
    private var lastPlaybackPosition: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fun getDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(requireContext(), id)

        channelAction = Action(
            R.id.player_action_channel.toLong(),
            getString(R.string.channel), null,
            getDrawable(R.drawable.subscriptions)
        )
        settingsAction = Action(
            R.id.player_action_settings.toLong(),
            getString(R.string.settings), null,
            getDrawable(R.drawable.video_settings_24)
        )
        supportAction = Action(
            R.id.player_action_support.toLong(),
            getString(R.string.support), null,
            getDrawable(R.drawable.lbc)
        )
        savedInstanceState?.let {
            lastPlaybackPosition = it.getLong(KEY_LAST_PLAYBACK_POSITION)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(Color.BLACK)
        surfaceView.keepScreenOn = true
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { uiState ->
                        with(playbackGlue) {
                            title = uiState.title
                            subtitle = uiState.subtitle
                        }
                        if (uiState.isLoadingData) {
                            progressBarManager.show()
                        } else {
                            progressBarManager.hide()
                        }
                        uiState.errorMessage?.let(::goToErrorScreen).also {
                            viewModel.errorMessageShown()
                        }
                    }
                }
                launch {
                    viewModel.uiState
                        .distinctUntilChangedBy { it.preferredVideoSize }
                        .map { it.preferredVideoSize }
                        .filterNotNull()
                        .collect(::setPreferredVideoSize)
                }
                launch {
                    viewModel.uiState
                        .distinctUntilChangedBy { it.streamingUrl }
                        .map { it.streamingUrl }
                        .filterNotNull()
                        .collect(::play)
                }
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
        player = ExoPlayer.Builder(context)
            .setLoadControl(
                DefaultLoadControl.Builder()
                    .setBufferDurationsMs(
                        MIN_BUFFER_MS,
                        MAX_BUFFER_MS,
                        BUFFER_FOR_PLAYBACK_MS,
                        DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
                    )
                    .build()
            )
            .build()
            .apply {
                addListener(object : Player.Listener {
                    override fun onTracksInfoChanged(tracksInfo: TracksInfo) {
                        viewModel.uiState.value.preferredVideoSize?.let {
                            setPreferredVideoSize(it)
                        }
                    }
                })
            }
        mediaSession = MediaSession.Builder(context, player).build()
        playbackGlue = PlaybackGlue(
            context,
            LeanbackPlayerAdapter(
                context,
                player,
                PLAYER_CONTROL_UPDATE_PERIOD_MILLIS
            )
        ).apply {
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
        if (streamUrl.videoIsTranscoded) {
            val dataSourceFactory = DefaultHttpDataSource.Factory()
            val hlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
                .setLoadErrorHandlingPolicy(StreamLoadErrorPolicy())
                .createMediaSource(MediaItem.fromUri(streamUrl.url))
            player.setMediaSource(hlsMediaSource)
        } else {
            player.setMediaItem(MediaItem.fromUri(streamUrl.url))
        }
        player.prepare()
        player.play()
        lastPlaybackPosition?.let { player.seekTo(it) }
    }

    private fun setPreferredVideoSize(preferredVideoSize: Size) {
        val videoTrackGroup = player.currentTracksInfo
            .trackGroupInfos.find {
                it.trackType == C.TRACK_TYPE_VIDEO
            }?.trackGroup ?: return
        val indexOfTargetFormat = (0 until videoTrackGroup.length)
            .map { videoTrackGroup.getFormat(it) }
            .indexOfFirst { it.height <= preferredVideoSize.height }
        player.trackSelectionParameters = player.trackSelectionParameters
            .buildUpon()
            .setTrackSelectionOverrides(
                if (indexOfTargetFormat == -1) {
                    TrackSelectionOverrides.Builder()
                        .clearOverride(videoTrackGroup)
                        .build()
                } else {
                    TrackSelectionOverrides.Builder()
                        .addOverride(
                            TrackSelectionOverrides.TrackSelectionOverride(
                                videoTrackGroup,
                                listOf(indexOfTargetFormat)
                            )
                        ).build()
                }
            ).build()
    }

    private fun goToChannelScreen() {
        viewModel.uiState.value.channelId?.let {
            navController.navigate(
                VideoPlayerFragmentDirections
                    .actionVideoPlayerFragmentToChannelVideosFragment(it),
                NavOptions.Builder()
                    .setPopUpTo(R.id.channelVideosFragment, true)
                    .build()
            )
        }
    }

    private fun goToSupportScreen() {
        viewModel.uiState.value.claimId?.let {
            navController.navigate(
                VideoPlayerFragmentDirections.actionVideoPlayerFragmentToSupportFragment(it)
            )
        }
    }

    private fun goToSettingsScreen() {
        player.currentTracksInfo
            .takeIf { it.trackGroupInfos.isNotEmpty() }
            ?.toBundle()
            ?.let {
                playbackGlue.pause()
                hideControlsOverlay(true)
                navController.navigate(
                    VideoPlayerFragmentDirections
                        .actionVideoPlayerFragmentToVideoQualitySettingsDialogFragment(it)
                )
                val navBackStackEntry = navController
                    .getBackStackEntry(R.id.videoQualitySettingsDialogFragment)
                navBackStackEntry.lifecycle.addObserver(
                    object : LifecycleEventObserver {
                        override fun onStateChanged(
                            source: LifecycleOwner,
                            event: Lifecycle.Event,
                        ) {
                            if (event == Lifecycle.Event.ON_DESTROY) {
                                playbackGlue.play()
                                hideControlsOverlay(true)
                                navBackStackEntry.lifecycle.removeObserver(this)
                            }
                        }
                    }
                )
            }
    }

    private fun goToErrorScreen(message: String?) {
        navController.navigate(NavGraphDirections.actionGlobalErrorFragment(message))
    }

    private inner class PlaybackGlue(context: Context, playerAdapter: LeanbackPlayerAdapter) :
        PlaybackTransportControlGlue<LeanbackPlayerAdapter>(context, playerAdapter) {

        override fun onCreatePrimaryActions(primaryActionsAdapter: ArrayObjectAdapter) {
            with(primaryActionsAdapter) {
                add(channelAction)
                add(settingsAction)
                add(supportAction)
            }
        }

        override fun onActionClicked(action: Action) {
            super.onActionClicked(action)
            when (action) {
                channelAction -> goToChannelScreen()
                supportAction -> goToSupportScreen()
                settingsAction -> goToSettingsScreen()
            }
        }
    }
}

private class StreamLoadErrorPolicy : DefaultLoadErrorHandlingPolicy() {
    override fun getMinimumLoadableRetryCount(dataType: Int) = Int.MAX_VALUE
}
