package app.newproj.lbrytv.viewmodel

import androidx.leanback.widget.Row
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.newproj.lbrytv.data.repo.HomeRowRepository
import app.newproj.lbrytv.util.mapRow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(homeRowRepo: HomeRowRepository) : ViewModel() {
    val rows: Flow<PagingData<Row>> = homeRowRepo
        .rowModels()
        .mapRow()
        .cachedIn(viewModelScope)
}
