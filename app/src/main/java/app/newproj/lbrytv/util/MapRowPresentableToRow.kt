package app.newproj.lbrytv.util

import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.Row
import androidx.paging.PagingData
import app.newproj.lbrytv.data.comparator.CardPresentableComparator
import app.newproj.lbrytv.data.dto.ChannelDetailsOverview
import app.newproj.lbrytv.data.dto.PagingDataList
import app.newproj.lbrytv.data.dto.PagingDataListRow
import app.newproj.lbrytv.data.dto.RowPresentable
import app.newproj.lbrytv.presenter.CardPresenterSelector
import kotlinx.coroutines.flow.Flow

fun Flow<PagingData<RowPresentable>>.mapRow(): Flow<PagingData<Row>> {
    return mapPagingData {
        when (it) {
            is PagingDataList -> {
                val adapter = PagingDataAdapter(CardPresenterSelector(), CardPresentableComparator)
                PagingDataListRow(it.id, adapter, it)
            }
            is ChannelDetailsOverview -> DetailsOverviewRow(it).apply { id = it.id }
        }
    }
}
