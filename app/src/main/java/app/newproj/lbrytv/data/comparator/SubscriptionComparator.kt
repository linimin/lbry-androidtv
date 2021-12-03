package app.newproj.lbrytv.data.comparator

import androidx.recyclerview.widget.DiffUtil
import app.newproj.lbrytv.data.entity.Subscription

object SubscriptionComparator : DiffUtil.ItemCallback<Subscription>() {
    override fun areItemsTheSame(oldItem: Subscription, newItem: Subscription): Boolean {
        return oldItem.claimId == newItem.claimId
    }

    override fun areContentsTheSame(oldItem: Subscription, newItem: Subscription): Boolean {
        return oldItem == newItem
    }
}
