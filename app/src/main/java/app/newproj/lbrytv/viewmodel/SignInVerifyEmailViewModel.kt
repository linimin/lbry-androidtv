package app.newproj.lbrytv.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.newproj.lbrytv.data.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInVerifyEmailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val email: String = savedStateHandle.get("email")!!

    private val _uiState = MutableStateFlow<UiState>(UiState.WaitingForVerification)
    val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch {
            try {
                userRepository.user(email)
                _uiState.emit(UiState.Completed)
            } catch (e: Exception) {
                _uiState.emit(UiState.Error(e))
            }
        }
    }

    sealed class UiState {
        object WaitingForVerification : UiState()
        object Completed : UiState()
        data class Error(val error: Exception) : UiState()
    }
}
