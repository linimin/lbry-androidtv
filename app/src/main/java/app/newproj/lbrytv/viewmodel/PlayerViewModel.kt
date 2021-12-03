package app.newproj.lbrytv.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import app.newproj.lbrytv.data.repo.ClaimRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    claimRepo: ClaimRepository,
) : ViewModel() {

    private val claimId: String = savedStateHandle["claimId"]!!

    val claim = claimRepo.claim(claimId)
}
