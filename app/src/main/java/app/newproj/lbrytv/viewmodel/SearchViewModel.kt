package app.newproj.lbrytv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import app.newproj.lbrytv.data.dto.CardPresentable
import app.newproj.lbrytv.data.repo.RelatedClaimRepository
import app.newproj.lbrytv.util.cast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    relatedClaimRepo: RelatedClaimRepository,
) : ViewModel() {

    private val query = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResult: Flow<PagingData<CardPresentable>> = query
        .filterNotNull()
        .distinctUntilChanged()
        .flatMapLatest { query ->
            relatedClaimRepo
                .relatedClaims(query)
                .cast()
        }

    fun search(query: String?) {
        viewModelScope.launch {
            this@SearchViewModel.query.emit(query)
        }
    }
}
