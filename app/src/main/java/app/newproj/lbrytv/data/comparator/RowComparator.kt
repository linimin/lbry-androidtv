package app.newproj.lbrytv.data.comparator

import android.annotation.SuppressLint
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.Row
import androidx.recyclerview.widget.DiffUtil

object RowComparator : DiffUtil.ItemCallback<Row>() {
    override fun areItemsTheSame(oldItem: Row, newItem: Row): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Row, newItem: Row): Boolean {
        return when {
            oldItem::class != newItem::class -> false
            oldItem is DetailsOverviewRow && newItem is DetailsOverviewRow -> {
                oldItem.item == newItem.item
            }
            else -> true
        }
    }
}
