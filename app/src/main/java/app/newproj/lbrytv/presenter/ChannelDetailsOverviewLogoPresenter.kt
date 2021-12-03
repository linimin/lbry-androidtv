package app.newproj.lbrytv.presenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.leanback.widget.DetailsOverviewLogoPresenter
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.Presenter
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.ChannelDetailsOverview
import com.bumptech.glide.Glide

class ChannelDetailsOverviewLogoPresenter : DetailsOverviewLogoPresenter() {
    override fun onCreateView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.channel_details_overview_logo, parent, false)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        require(viewHolder is ViewHolder)
        val detailsOverviewRow = item as DetailsOverviewRow
        val channelDetailsOverview = detailsOverviewRow.item as ChannelDetailsOverview
        val imageView = viewHolder.view as ImageView
        Glide.with(imageView)
            .load(channelDetailsOverview.claim.thumbnail)
            .circleCrop()
            .into(imageView)
        viewHolder.parentPresenter.notifyOnBindLogo(viewHolder.parentViewHolder)
    }

    override fun isBoundToImage(viewHolder: ViewHolder, row: DetailsOverviewRow): Boolean {
        val channelDetailsOverview = row.item as ChannelDetailsOverview
        return channelDetailsOverview.claim.thumbnail != null
    }
}
