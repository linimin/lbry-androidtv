package app.newproj.lbrytv.util

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T : Any, R : Any> Flow<PagingData<T>>.mapPagingData(transform: suspend (T) -> R): Flow<PagingData<R>> {
    return map { it.map(transform) }
}
