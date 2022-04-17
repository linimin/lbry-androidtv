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
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackGroup
import androidx.media3.common.TrackSelectionOverrides
import androidx.media3.common.TracksInfo
import androidx.media3.common.VideoSize
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
import androidx.preference.PreferenceManager
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
    private lateinit var supportAction: Action
    private lateinit var channelAction: Action
    private lateinit var settingsAction: Action
    private val playerListener = object : Player.Listener {
        override fun onTracksInfoChanged(tracksInfo: TracksInfo) {
            val trackGroup = tracksInfo.trackGroupInfos
                .find { it.trackType == C.TRACK_TYPE_VIDEO }
                ?.trackGroup
                ?: return
            val preference = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val size = preference
                .getString(R.id.video_quality_settings.toString(), null)
                ?.toInt() ?: VIDEO_QUALITY_AUTO_SIZE
            if (size != VIDEO_QUALITY_AUTO_SIZE) {
                val index = (0 until trackGroup.length)
                    .map { trackGroup.getFormat(it) }
                    .indexOfFirst { (it.width * it.height) <= size }
                if (index >= 0) {
                    setTrackSelectionOverrides(trackGroup, index)
                } else {
                    setTrackSelectionOverrides(trackGroup, null)
                }
            } else {
                setTrackSelectionOverrides(trackGroup, null)
            }
        }

        private fun setTrackSelectionOverrides(trackGroup: TrackGroup, index: Int?) {
            val overrides = if (index != null) {
                val format = trackGroup.getFormat(index)
                PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .edit()
                    .putString(
                        R.id.video_quality_settings.toString(),
                        (format.width * format.height).toString()
                    )
                    .apply()
                TrackSelectionOverrides.Builder()
                    .addOverride(
                        TrackSelectionOverrides.TrackSelectionOverride(
                            trackGroup,
                            listOf(index)
                        )
                    )
                    .build()
            } else {
                PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .edit()
                    .putString(
                        R.id.video_quality_settings.toString(),
                        VIDEO_QUALITY_AUTO_SIZE.toString()
                    )
                    .apply()
                TrackSelectionOverrides.Builder()
                    .clearOverride(trackGroup)
                    .build()
            }
            player.trackSelectionParameters = player.trackSelectionParameters
                .buildUpon()
                .setTrackSelectionOverrides(overrides)
                .build()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channelAction = Action(
            R.id.guided_action_channel.toLong(),
            null, null,
            ContextCompat.getDrawable(requireContext(), R.drawable.subscriptions)
        )
        supportAction = Action(
            R.id.guided_action_support.toLong(),
            null, null,
            ContextCompat.getDrawable(requireContext(), R.drawable.lbc)
        )
        settingsAction = Action(
            R.id.guided_action_settings.toLong(),
            null, null,
            ContextCompat.getDrawable(requireContext(), R.drawable.video_settings_24)
        )
        savedInstanceState?.let {
            lastPlaybackPosition = it.getLong(KEY_LAST_PLAYBACK_POSITION)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(Color.BLACK)
        surfaceView.keepScreenOn = true
        require(view is ViewGroup)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                playbackGlue.apply {
                    title = it.title
                    subtitle = it.subtitle
                }
                if (it.isLoadingData) {
                    progressBarManager.show()
                } else {
                    progressBarManager.hide()
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
        player = ExoPlayer.Builder(context)
            .setLoadControl(
                DefaultLoadControl.Builder()
                    .setBufferDurationsMs(
                        120_000,
                        120_000,
                        1500,
                        DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
                    )
                    .build()
            )
            .build()
            .apply {
                addListener(playerListener)
            }
        mediaSession = MediaSession.Builder(context, player).build()
        val playerAdapter = LeanbackPlayerAdapter(
            context,
            player,
            PLAYER_CONTROL_UPDATE_PERIOD_MILLIS
        )
        playbackGlue = CustomPlaybackTransportControlGlue(context, playerAdapter).apply {
            host = VideoSupportFragmentGlueHost(this@VideoPlayerFragment)
            isSeekEnabled = true
        }
        adapter = ArrayObjectAdapter(playbackGlue.playbackRowPresenter).apply {
            add(playbackGlue.controlsRow)
        }
        player.addListener(object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                lastPlaybackPosition?.let { player.seekTo(it) }
            }
        })
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

    private fun setHighQualityEnabled(enabled: Boolean) {
        if (enabled) {
            player.trackSelectionParameters = player.trackSelectionParameters
                .buildUpon()
                .clearVideoSizeConstraints()
                .build();
        } else {
            player.trackSelectionParameters = player.trackSelectionParameters
                .buildUpon()
                .setMaxVideoSizeSd()
                .build();
        }
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
        player.currentTracksInfo.takeIf { it.trackGroupInfos.isNotEmpty() }?.let {
            navController.navigate(
                VideoPlayerFragmentDirections.actionVideoPlayerFragmentToQualitySettingsFragment(
                    it.toBundle()
                )
            )
        }
    }

    private fun goToErrorScreen(message: String?) {
        navController.navigate(NavGraphDirections.actionGlobalErrorFragment(message))
        viewModel.errorMessageShown()
    }

    private inner class CustomPlaybackTransportControlGlue(
        context: Context,
        playerAdapter: LeanbackPlayerAdapter,
    ) : PlaybackTransportControlGlue<LeanbackPlayerAdapter>(context, playerAdapter) {
        override fun onCreatePrimaryActions(primaryActionsAdapter: ArrayObjectAdapter) {
            super.onCreatePrimaryActions(primaryActionsAdapter)
            primaryActionsAdapter.add(channelAction)
            primaryActionsAdapter.add(settingsAction)
            primaryActionsAdapter.add(supportAction)
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
    override fun getMinimumLoadableRetryCount(dataType: Int): Int {
        return Int.MAX_VALUE
    }
}
