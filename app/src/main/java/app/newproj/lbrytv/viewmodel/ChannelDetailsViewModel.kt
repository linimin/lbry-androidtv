package app.newproj.lbrytv.viewmodel

import android.net.Uri
import androidx.leanback.widget.Row
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.newproj.lbrytv.data.entity.Claim
import app.newproj.lbrytv.data.repo.ChannelDetailsRowRepository
import app.newproj.lbrytv.data.repo.ClaimRepository
import app.newproj.lbrytv.util.mapRow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val claimRepo: ClaimRepository,
    private val rowRepo: ChannelDetailsRowRepository,
) : ViewModel() {

    private val claimId: String = savedStateHandle["claimId"]!!

    private val claim: SharedFlow<Claim> = claimRepo
        .claim(claimId)
        .distinctUntilChanged()
        .shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    val backgroundImageUri: Flow<Uri?> = claim.map { it.cover }

    @OptIn(ExperimentalCoroutinesApi::class)
    val rows: Flow<PagingData<Row>> = claim
        .flatMapLatest(rowRepo::rows)
        .mapRow()
        .cachedIn(viewModelScope)

    fun subscribe() {
        viewModelScope.launch {
            claimRepo.addSubscription(claim.replayCache.first())
        }
    }

    fun unsubscribe() {
        viewModelScope.launch {
            claimRepo.removeSubscription(claim.replayCache.first())
        }
    }
}
