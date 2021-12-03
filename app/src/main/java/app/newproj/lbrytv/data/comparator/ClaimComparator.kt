package app.newproj.lbrytv.data.comparator

import androidx.recyclerview.widget.DiffUtil
import app.newproj.lbrytv.data.entity.Claim

object ClaimComparator : DiffUtil.ItemCallback<Claim>() {
    override fun areItemsTheSame(oldItem: Claim, newItem: Claim): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Claim, newItem: Claim): Boolean {
        return oldItem == newItem
    }
}
