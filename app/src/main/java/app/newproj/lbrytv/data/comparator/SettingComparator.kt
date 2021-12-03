package app.newproj.lbrytv.data.comparator

import androidx.recyclerview.widget.DiffUtil
import app.newproj.lbrytv.data.entity.Setting

object SettingComparator : DiffUtil.ItemCallback<Setting>() {
    override fun areItemsTheSame(oldItem: Setting, newItem: Setting): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Setting, newItem: Setting): Boolean {
        return oldItem == newItem
    }
}
