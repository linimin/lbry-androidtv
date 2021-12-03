package app.newproj.lbrytv.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.newproj.lbrytv.data.entity.Claim
import app.newproj.lbrytv.media.MediaPlayerGlue
import app.newproj.lbrytv.util.streamingUrl
import app.newproj.lbrytv.viewmodel.PlayerViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val PLAYER_CONTROL_UPDATE_PERIOD_MILLIS = 100

@AndroidEntryPoint
class VideoPlayerFragment : VideoSupportFragment() {
    private val viewModel: PlayerViewModel by viewModels()
    private lateinit var player: Player
    private lateinit var playerGlue: MediaPlayerGlue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = requireContext()
        player = SimpleExoPlayer.Builder(context).build()
        playerGlue = MediaPlayerGlue(
            context,
            LeanbackPlayerAdapter(context, player, PLAYER_CONTROL_UPDATE_PERIOD_MILLIS)
        ).apply {
            host = VideoSupportFragmentGlueHost(this@VideoPlayerFragment)
            host.setOnKeyInterceptListener(KeyHandler())
        }
        adapter = ArrayObjectAdapter(playerGlue.playbackRowPresenter).apply {
            add(playerGlue.controlsRow)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.claim.collectLatest(::play)
        }
    }

    private fun play(claim: Claim) {
        with(player) {
            val mediaItem = MediaItem.fromUri(claim.streamingUrl())
            setMediaItem(mediaItem)
            prepare()
        }
        with(playerGlue) {
            title = claim.title
            playWhenPrepared()
        }
    }

    override fun onPause() {
        super.onPause()
        playerGlue.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    override fun onError(errorCode: Int, errorMessage: CharSequence?) {
        VideoPlayerFragmentDirections
            .actionGlobalErrorFragment(errorMessage?.toString())
            .let(findNavController()::navigate)
    }

    private inner class KeyHandler : View.OnKeyListener {
        override fun onKey(view: View, keyCode: Int, keyEvent: KeyEvent): Boolean {
            when {
                playerGlue.host.isControlsOverlayVisible -> return false
                keyCode == KeyEvent.KEYCODE_DPAD_CENTER && keyEvent.action == KeyEvent.ACTION_DOWN -> {
                    if (playerGlue.isPlaying) playerGlue.pause() else playerGlue.play()
                    return true
                }
                keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && keyEvent.action == KeyEvent.ACTION_DOWN -> {
                    playerGlue.skipForward()
                    preventControlsOverlay()
                    return true
                }
                keyCode == KeyEvent.KEYCODE_DPAD_LEFT && keyEvent.action == KeyEvent.ACTION_DOWN -> {
                    playerGlue.skipBackward()
                    preventControlsOverlay()
                    return true
                }
                else -> return false
            }
        }

        // Workaround used to prevent controls overlay from showing and taking focus.
        private fun preventControlsOverlay() = view?.postDelayed({
            with(playerGlue.host) {
                showControlsOverlay(false)
                hideControlsOverlay(false)
            }
        }, 10)
    }
}
