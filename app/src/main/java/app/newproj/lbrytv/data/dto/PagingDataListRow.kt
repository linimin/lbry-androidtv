package app.newproj.lbrytv.data.dto

import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.ListRow

class PagingDataListRow(
    id: Long,
    val pagingDataAdapter: PagingDataAdapter<CardPresentable>,
    val pagingDataList: PagingDataList,
) : ListRow(id, null, pagingDataAdapter)
