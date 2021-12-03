package app.newproj.lbrytv.presenter

import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowHeaderPresenter
import app.newproj.lbrytv.data.dto.PagingDataListRow

class PagingDataListRowHeaderPresenter : RowHeaderPresenter() {
    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        require(item is PagingDataListRow)
        val context = viewHolder.view.context
        item.headerItem = HeaderItem(context.getString(item.pagingDataList.title))
        super.onBindViewHolder(viewHolder, item)
    }
}
