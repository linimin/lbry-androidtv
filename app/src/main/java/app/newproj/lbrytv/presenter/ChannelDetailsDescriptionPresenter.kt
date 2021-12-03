package app.newproj.lbrytv.presenter

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import app.newproj.lbrytv.data.dto.ChannelDetailsOverview

/**
 * Renders the detailed description of a channel.
 */
class ChannelDetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {
    override fun onBindDescription(viewHolder: ViewHolder, item: Any) {
        val claim = (item as ChannelDetailsOverview).claim
        viewHolder.title.text = claim.title
        viewHolder.body.text = claim.description
    }
}
