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

import android.util.Size
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.media3.common.C
import androidx.media3.common.TracksInfo
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.datasource.AUTO_VIDEO_SIZE
import app.newproj.lbrytv.data.repo.PlayerSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class VideoQualitySettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val playerSettingsRepository: PlayerSettingsRepository,
) : ViewModel() {
    private val args = VideoPlayerSettingsFragmentArgs.fromSavedStateHandle(savedStateHandle)

    data class UiState(val videoSizes: List<VideoSizeUiState> = emptyList())

    data class VideoSizeUiState(
        val name: String?,
        @StringRes val nameRes: Int?,
        val size: Size,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        val tracksInfo = TracksInfo.CREATOR.fromBundle(args.tracksInfo)
        val videoTrackGroup = tracksInfo.trackGroupInfos
            .find { it.trackType == C.TRACK_TYPE_VIDEO }
            ?.trackGroup
        if (videoTrackGroup != null) {
            val autoVideoSize = VideoSizeUiState(
                name = null, nameRes = R.string.auto_quality,
                AUTO_VIDEO_SIZE
            )
            val videoSizes = (0 until videoTrackGroup.length)
                .map { videoTrackGroup.getFormat(it) }
                .map {
                    val size = Size(it.width, it.height)
                    VideoSizeUiState(
                        name = size.toString(), nameRes = null,
                        size
                    )
                }
            _uiState.update {
                it.copy(
                    videoSizes = listOf(autoVideoSize) + videoSizes
                )
            }
        }
    }
}
