package app.newproj.lbrytv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.newproj.lbrytv.data.repo.ClaimRepository
import app.newproj.lbrytv.data.repo.SettingRepository
import app.newproj.lbrytv.data.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val claimRepository: ClaimRepository,
) : ViewModel() {

    val stateFlow: StateFlow<State>
    private val actionFlow = MutableSharedFlow<Action>()

    init {
        stateFlow = actionFlow
            .filterIsInstance<Action.SignOut>()
            .distinctUntilChanged()
            .onEach { clearUserData() }
            .catch { State.Error(Exception(it)) }
            .map { State.SignOutSuccess }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), State.Initial)
    }

    fun signOut() {
        doAction(Action.SignOut)
    }

    private suspend fun clearUserData() {
        userRepository.deleteUser()
        claimRepository.deleteUserRelatedClaims()
    }

    private fun doAction(action: Action) {
        viewModelScope.launch { actionFlow.emit(action) }
    }

    sealed class State {
        object Initial : State()
        object SignOutSuccess : State()
        data class Error(val exception: Exception) : State()
    }

    sealed class Action {
        object SignOut : Action()
    }
}
