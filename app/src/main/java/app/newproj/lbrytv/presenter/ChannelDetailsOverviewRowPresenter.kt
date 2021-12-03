package app.newproj.lbrytv.presenter

import androidx.leanback.widget.*
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.ChannelDetailsOverview

class ChannelDetailsOverviewRowPresenter() : FullWidthDetailsOverviewRowPresenter(
    ChannelDetailsDescriptionPresenter(),
    ChannelDetailsOverviewLogoPresenter()
) {

    override fun onBindRowViewHolder(holder: RowPresenter.ViewHolder, item: Any) {
        super.onBindRowViewHolder(holder, item)
        val detailsOverviewRow = item as DetailsOverviewRow
        val channelDetails = detailsOverviewRow.item as ChannelDetailsOverview
        detailsOverviewRow.actionsAdapter = ArrayObjectAdapter().apply {
            val context = holder.view.context
            add(
                Action(
                    R.id.ui_jump_to_latest_videos.toLong(),
                    context.getString(R.string.latest_videos)
                )
            )
            if (channelDetails.isFollowing) {
                add(
                    Action(
                        R.id.ui_unsubscribe.toLong(),
                        context.getString(R.string.unsubscribe),
                    )
                )
            } else {
                add(
                    Action(
                        R.id.ui_subscribe.toLong(),
                        context.getString(R.string.subscribe)
                    )
                )
            }
        }
    }
}
