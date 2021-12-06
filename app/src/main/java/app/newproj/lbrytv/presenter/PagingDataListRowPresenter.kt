package app.newproj.lbrytv.presenter

import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.RowPresenter
import app.newproj.lbrytv.data.dto.PagingDataListRow
import app.newproj.lbrytv.hiltmodule.MainCoroutineScope
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@FragmentScoped
class PagingDataListRowPresenter @Inject constructor(
    @MainCoroutineScope val mainScope: CoroutineScope,
) : ListRowPresenter() {

    init {
        headerPresenter = PagingDataListRowHeaderPresenter()
    }

    override fun onBindRowViewHolder(holder: RowPresenter.ViewHolder, item: Any) {
        super.onBindRowViewHolder(holder, item)
        val row = item as PagingDataListRow
        val job = mainScope.launch {
            row.pagingDataList.pagingDataFlow.collectLatest {
                row.pagingDataAdapter.submitData(it)
            }
        }
        holder.setFacet(Job::class.java, job)
    }

    override fun onUnbindRowViewHolder(holder: RowPresenter.ViewHolder) {
        super.onUnbindRowViewHolder(holder)
        (holder.getFacet(Job::class.java) as Job).cancel()
    }
}
