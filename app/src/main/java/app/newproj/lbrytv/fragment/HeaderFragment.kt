package app.newproj.lbrytv.fragment

import androidx.leanback.app.HeadersSupportFragment
import androidx.leanback.app.RowsSupportFragment

class HeaderFragment : HeadersSupportFragment() {
    var headerIconsFragment: RowsSupportFragment? = null
    private var headerViewSelectedListener: OnHeaderViewSelectedListener? = null

    init {
        super.setOnHeaderViewSelectedListener { viewHolder, row ->
            headerIconsFragment?.selectedPosition = selectedPosition
            headerViewSelectedListener?.onHeaderSelected(viewHolder, row)
        }
    }

    override fun setAlignment(windowAlignOffsetTop: Int) {
        super.setAlignment(windowAlignOffsetTop)
        headerIconsFragment?.setAlignment(windowAlignOffsetTop)
    }

    override fun setOnHeaderViewSelectedListener(listener: OnHeaderViewSelectedListener?) {
        headerViewSelectedListener = listener
    }
}
