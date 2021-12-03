package app.newproj.lbrytv.data.comparator

import androidx.recyclerview.widget.DiffUtil
import app.newproj.lbrytv.data.dto.CardPresentable
import app.newproj.lbrytv.data.dto.ClaimCard
import app.newproj.lbrytv.data.dto.SettingCard

object CardPresentableComparator : DiffUtil.ItemCallback<CardPresentable>() {
    override fun areItemsTheSame(oldItem: CardPresentable, newItem: CardPresentable): Boolean {
        return when (oldItem) {
            is ClaimCard -> newItem is ClaimCard && oldItem.claim.id == newItem.claim.id
            is SettingCard -> newItem is SettingCard && oldItem.setting.id == newItem.setting.id
        }
    }

    override fun areContentsTheSame(oldItem: CardPresentable, newItem: CardPresentable): Boolean {
        return oldItem == newItem
    }
}
