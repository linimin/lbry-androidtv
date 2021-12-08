package app.newproj.lbrytv.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.RowPresenter
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.PagingDataListRow
import app.newproj.lbrytv.databinding.HeaderIconBinding

class HeaderIconPresenter : RowPresenter() {
    private class ViewHolder(
        val binding: HeaderIconBinding
    ) : RowPresenter.ViewHolder(binding.root) {

        val unselectAlpha: Float = view.resources.getFraction(
            R.fraction.lb_browse_header_unselect_alpha, 1, 1
        )
    }

    init {
        headerPresenter = null
    }

    override fun createRowViewHolder(parent: ViewGroup): RowPresenter.ViewHolder {
        return LayoutInflater.from(parent.context).run {
            ViewHolder(HeaderIconBinding.inflate(this, parent, false))
        }
    }

    override fun onBindRowViewHolder(viewHolder: RowPresenter.ViewHolder, item: Any) {
        require(viewHolder is ViewHolder)
        require(item is PagingDataListRow)
        item.pagingDataList.icon?.let {
            viewHolder.binding.icon.setImageResource(it)
        }
    }

    override fun isUsingDefaultSelectEffect() = false

    override fun onSelectLevelChanged(viewHolder: RowPresenter.ViewHolder) {
        with(viewHolder as ViewHolder) {
            view.alpha = unselectAlpha + selectLevel * (1 - unselectAlpha)
        }
    }
}
