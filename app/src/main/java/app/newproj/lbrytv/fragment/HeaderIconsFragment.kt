package app.newproj.lbrytv.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.leanback.app.RowsSupportFragment
import app.newproj.lbrytv.R

class HeaderIconsFragment : RowsSupportFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(
            inflater.cloneInContext(ContextThemeWrapper(context, R.style.HeaderIconsTheme)),
            container,
            savedInstanceState
        )
    }
}
