package app.newproj.lbrytv.media

import android.content.Context
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.media.PlayerAdapter
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

/** Default time used when skipping playback in milliseconds */
private val SKIP_PLAYBACK_MILLIS = TimeUnit.SECONDS.toMillis(10)

class MediaPlayerGlue(
    context: Context,
    adapter: PlayerAdapter
) : PlaybackTransportControlGlue<PlayerAdapter>(context, adapter) {

    private val actionRewind = PlaybackControlsRow.RewindAction(context)
    private val actionFastForward = PlaybackControlsRow.FastForwardAction(context)

    override fun onCreatePrimaryActions(adapter: ArrayObjectAdapter) {
        super.onCreatePrimaryActions(adapter)
        // Append rewind and fast forward actions to our player, keeping the play/pause actions
        // created by default by the glue
        adapter.add(actionRewind)
        adapter.add(actionFastForward)
    }

    override fun onActionClicked(action: Action?) {
        when (action) {
            actionRewind -> skipBackward()
            actionFastForward -> skipForward()
            else -> super.onActionClicked(action)
        }
    }

    fun skipForward(millis: Long = SKIP_PLAYBACK_MILLIS) {
        // Ensures we don't advance past the content duration (if set)
        with(playerAdapter) {
            val position = if (duration > 0) {
                min(duration, currentPosition + millis)
            } else {
                currentPosition + millis
            }
            seekTo(position)
        }
    }

    fun skipBackward(millis: Long = SKIP_PLAYBACK_MILLIS) {
        // Ensures we don't go below zero position
        with(playerAdapter) {
            val position = max(0, currentPosition - millis)
            seekTo(position)
        }
    }
}
