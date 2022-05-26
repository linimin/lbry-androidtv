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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.newproj.lbrytv.data.dto.StreamingUrl
import app.newproj.lbrytv.data.repo.PlayerSettingsRepository
import app.newproj.lbrytv.data.repo.StreamingUrlRepository
import app.newproj.lbrytv.data.repo.VideosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val videosRepository: VideosRepository,
    private val streamingUrlRepository: StreamingUrlRepository,
    private val playerSettingsRepository: PlayerSettingsRepository,
) : ViewModel() {
    private val args = VideoPlayerFragmentArgs.fromSavedStateHandle(savedStateHandle)

    data class UiState(
        val title: String? = null,
        val subtitle: String? = null,
        val streamingUrl: StreamingUrl? = null,
        val claimName: String? = null,
        val claimId: String? = null,
        val channelId: String? = null,
        val errorMessage: String? = null,
        val isLoadingData: Boolean = true,
        val preferredVideoSize: Size? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                videosRepository.video(args.claimId)
                    .distinctUntilChanged()
                    .collectLatest { video ->
                        val streamingUrl = video.claim.permanentUrl
                            ?.let { streamingUrlRepository.streamingUrl(it) }
                        _uiState.update {
                            it.copy(
                                title = video.claim.title,
                                subtitle = video.claim.channelName,
                                streamingUrl = streamingUrl,
                                claimName = video.claim.name,
                                claimId = video.claim.id,
                                channelId = video.claim.channelId,
                                isLoadingData = false,
                            )
                        }
                    }
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(errorMessage = error.localizedMessage)
                }
            }
        }
        viewModelScope.launch {
            playerSettingsRepository.preferredVideoSize().collectLatest { videoSize ->
                _uiState.update {
                    it.copy(preferredVideoSize = videoSize)
                }
            }
        }
    }

    fun errorMessageShown() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }
}
