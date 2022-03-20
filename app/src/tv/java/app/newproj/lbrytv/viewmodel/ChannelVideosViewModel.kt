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

package app.newproj.lbrytv.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import app.newproj.lbrytv.data.dto.Channel
import app.newproj.lbrytv.data.dto.Video
import app.newproj.lbrytv.data.repo.ChannelRepository
import app.newproj.lbrytv.data.repo.VideoRepository
import app.newproj.lbrytv.fragment.ChannelVideosFragmentArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelVideosViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    channelRepo: ChannelRepository,
    private val videoRepo: VideoRepository,
) : ViewModel() {
    private val args = ChannelVideosFragmentArgs.fromSavedStateHandle(savedStateHandle)

    sealed class UiState {
        object Loading : UiState()
        data class Idle(val channel: Channel) : UiState()
        data class Error(val error: Throwable) : UiState()
    }

    private val channel: SharedFlow<Channel> = channelRepo
        .channel(args.channelId)
        .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch {
            channel.collect() {
                _uiState.value = UiState.Idle(it)
            }
        }
    }

    fun videos(): Flow<PagingData<Video>> = videoRepo.channelVideos(args.channelId)
}
