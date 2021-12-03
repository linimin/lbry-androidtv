package app.newproj.lbrytv.data.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.ClaimCard
import app.newproj.lbrytv.data.dto.PagingDataList
import app.newproj.lbrytv.data.dto.RowPresentable
import app.newproj.lbrytv.data.dto.SettingCard
import app.newproj.lbrytv.util.cast
import app.newproj.lbrytv.util.mapPagingData
import javax.inject.Inject

class HomeRowPagingSource @Inject constructor(
    private val claimRepo: ClaimRepository,
    private val settingRepository: SettingRepository,
) : PagingSource<Int, RowPresentable>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RowPresentable> {
        var id = -1L

        val subscribedContent = claimRepo.subscribedContent().mapPagingData { ClaimCard(it) }
        val subscribedContentRow =
            PagingDataList(++id, R.string.subscribed_content, subscribedContent.cast())

        val trending = claimRepo.trending().mapPagingData { ClaimCard(it) }
        val trendingRow = PagingDataList(++id, R.string.trending, trending.cast())
        
        val suggestedChannels = claimRepo.suggestedChannels().mapPagingData { ClaimCard(it) }
        val suggestedChannelRow =
            PagingDataList(++id, R.string.suggested_channels, suggestedChannels.cast())

        val subscription = claimRepo.subscription().mapPagingData { ClaimCard(it) }
        val subscriptionRow =
            PagingDataList(++id, R.string.subscriptions, subscription.cast())

        val settings = settingRepository.settings().mapPagingData { SettingCard(it) }
        val settingRow = PagingDataList(++id, R.string.settings, settings.cast())

        return LoadResult.Page(
            listOf(
                trendingRow,
                subscribedContentRow,
                subscriptionRow,
                suggestedChannelRow,
                settingRow
            ), null, null
        )
    }

    override fun getRefreshKey(state: PagingState<Int, RowPresentable>): Int? = null
}
