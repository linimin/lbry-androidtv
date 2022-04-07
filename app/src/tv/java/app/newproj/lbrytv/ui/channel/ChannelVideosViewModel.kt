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

package app.newproj.lbrytv.ui.channel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import app.newproj.lbrytv.data.dto.ChannelUiState
import app.newproj.lbrytv.data.dto.ChannelWithVideos
import app.newproj.lbrytv.data.dto.VideoUiState
import app.newproj.lbrytv.usecase.FollowUnfollowChannelUseCase
import app.newproj.lbrytv.usecase.GetChannelWithVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ChannelVideosViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getChannelWithVideosUseCase: GetChannelWithVideosUseCase,
    private val followUnfollowChannelUseCase: FollowUnfollowChannelUseCase,
) : ViewModel() {
    private val args = ChannelVideosFragmentArgs.fromSavedStateHandle(savedStateHandle)

    data class UiState(
        val channel: ChannelUiState? = null,
        val errorMessage: String? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val channelWithVideos = MutableSharedFlow<ChannelWithVideos>(replay = 1)
    private val channel: Flow<ChannelUiState> = channelWithVideos
        .flatMapLatest { it.channel }
        .map {
            ChannelUiState(it.id, it.claim.thumbnail, it.claim.title, it.claim.name, it.isFollowing)
        }
    val videos: Flow<PagingData<VideoUiState>> = channelWithVideos
        .flatMapLatest { it.videos }
        .map { pagingData ->
            pagingData.map { video ->
                VideoUiState.fromVideo(video)
            }
        }

    init {
        viewModelScope.launch {
            try {
                channelWithVideos.emit(getChannelWithVideosUseCase(args.channelId))
                channel.collect { channel ->
                    _uiState.update {
                        it.copy(channel = channel)
                    }
                }
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(errorMessage = error.localizedMessage)
                }
            }
        }
    }

    fun followUnfollow() {
        viewModelScope.launch {
            try {
                _uiState.value.channel?.let {
                    followUnfollowChannelUseCase(it.id)
                }
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(errorMessage = error.localizedMessage)
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
