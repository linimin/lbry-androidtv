package app.newproj.lbrytv.data.dto

import app.newproj.lbrytv.data.entity.Claim

data class ChannelDetailsOverview(
    val id: Long,
    val claim: Claim,
    val isFollowing: Boolean
) : RowPresentable
