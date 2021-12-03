package app.newproj.lbrytv.data.dto

import androidx.annotation.StringRes
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

data class PagingDataList(
    val id: Long,

    @StringRes
    val title: Int,

    val pagingDataFlow: Flow<PagingData<CardPresentable>>
) : RowPresentable
