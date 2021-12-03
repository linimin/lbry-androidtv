package app.newproj.lbrytv.data.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import androidx.room.Update

abstract class BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(item: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(items: List<T>): List<Long>

    @Update
    abstract suspend fun update(item: T)

    @Update
    abstract suspend fun update(items: List<T>)

    suspend fun insertOrUpdate(items: List<T>) {
        insertOrUpdate(items, ::insert, ::update)
    }

    suspend fun insertOrUpdate(item: T) {
        insertOrUpdate(item, ::insert, ::update)
    }

    @Transaction
    open suspend fun <T> insertOrUpdate(
        item: T,
        insert: suspend (T) -> Long,
        update: suspend (T) -> Unit,
    ) {
        if (insert(item) == -1L) {
            update(item)
        }
    }

    @Transaction
    open suspend fun <T> insertOrUpdate(
        items: List<T>,
        insert: suspend (List<T>) -> List<Long>,
        update: suspend (List<T>) -> Unit,
    ) {
        val insertResult = insert(items)
        val updateList = mutableListOf<T>()

        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateList.add(items[i])
        }

        if (updateList.isNotEmpty()) update(updateList)
    }
}
