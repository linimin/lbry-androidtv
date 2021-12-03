package app.newproj.lbrytv.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ErrorViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
    val message: String? = savedStateHandle.get("message")
}
