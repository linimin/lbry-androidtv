package app.newproj.lbrytv.hiltmodule

import androidx.fragment.app.Fragment
import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.Row
import androidx.lifecycle.lifecycleScope
import app.newproj.lbrytv.data.comparator.RowComparator
import app.newproj.lbrytv.data.dto.PagingDataListRow
import app.newproj.lbrytv.presenter.ChannelDetailsDescriptionPresenter
import app.newproj.lbrytv.presenter.ChannelDetailsOverviewRowPresenter
import app.newproj.lbrytv.presenter.PagingDataListRowPresenter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
object RowsPagingDataAdapterModule {
    @Provides
    @FragmentScoped
    fun rowsPagingDataAdapter(fragment: Fragment): PagingDataAdapter<Row> {
        val presenterSelector = ClassPresenterSelector().apply {
            addClassPresenter(
                DetailsOverviewRow::class.java, ChannelDetailsOverviewRowPresenter()
            )
            addClassPresenter(
                PagingDataListRow::class.java, PagingDataListRowPresenter(fragment.lifecycleScope)
            )
        }
        return PagingDataAdapter(presenterSelector, RowComparator)
    }
}
