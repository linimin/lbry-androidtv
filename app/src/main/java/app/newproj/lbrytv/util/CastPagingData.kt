package app.newproj.lbrytv.util

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

fun <T : R, R : Any> Flow<PagingData<T>>.cast(): Flow<PagingData<R>> {
    return mapPagingData { it }
}
