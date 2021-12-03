package app.newproj.lbrytv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.newproj.lbrytv.util.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInEmailInputViewModel @Inject constructor() : ViewModel() {
    val stateFlow: StateFlow<State>
    private val actionFlow = MutableSharedFlow<Action>()

    init {
        stateFlow = actionFlow
            .filterIsInstance<Action.SetEmail>()
            .distinctUntilChanged()
            .map { validateEmail(it.email) }
            .onStart { emit(State.EmptyEmail) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), State.Initial)
    }

    fun setEmail(email: String?) {
        doAction(Action.SetEmail(email))
    }

    private fun doAction(action: Action) {
        viewModelScope.launch { actionFlow.emit(action) }
    }

    private fun validateEmail(email: String?): State {
        return when {
            email?.isEmpty() == true -> State.EmptyEmail
            email?.isValidEmail() == true -> State.ValidEmail(email)
            else -> State.InvalidEmail(email)
        }
    }

    sealed class State {
        object Initial: State()
        object EmptyEmail : State()
        data class InvalidEmail(val email: String?) : State()
        data class ValidEmail(val email: String) : State()
    }

    sealed class Action {
        data class SetEmail(val email: String?) : Action()
    }
}
