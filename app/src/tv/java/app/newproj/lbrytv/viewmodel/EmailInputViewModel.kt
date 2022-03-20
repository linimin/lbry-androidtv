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

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.Text
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SignInEmailInputViewModel @Inject constructor() : ViewModel() {
    sealed class UiState {
        data class Editing(
            val title: Text = Text.Resource(R.string.email_address_example),
            val description: Text = Text.Resource(R.string.enter_email_address),
            val isValidEmail: Boolean = false,
        ) : UiState()

        data class Finished(val email: String) : UiState()
    }

    private val _uiState = MutableSharedFlow<UiState>(1)
    val uiState: SharedFlow<UiState> = _uiState

    private var email: String? = null

    init {
        viewModelScope.launch {
            _uiState.emit(UiState.Editing())
        }
    }

    fun setEmail(email: String?): Boolean {
        this.email = email
        return validateEmail(email)
            .also { _uiState.tryEmit(it) }
            .isValidEmail
    }

    private fun validateEmail(email: String?): UiState.Editing {
        return when {
            email.isNullOrEmpty() -> UiState.Editing()
            email.isValidEmail() -> UiState.Editing(
                title = Text.Raw(email),
                description = Text.Resource(R.string.enter_email_address),
                isValidEmail = true
            )
            else -> UiState.Editing(
                title = Text.Raw(email),
                description = Text.Resource(R.string.invalid_email_address),
                isValidEmail = false
            )
        }
    }

    fun enter() {
        email?.let {
            if (it.isValidEmail()) {
                viewModelScope.launch {
                    _uiState.emit(UiState.Finished(it))
                    _uiState.emit(validateEmail(it))
                }
            }
        }
    }
}

private fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
