package app.newproj.lbrytv.fragment

import androidx.fragment.app.viewModels
import androidx.leanback.app.SearchSupportFragment
import app.newproj.lbrytv.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : SearchSupportFragment() {
    private val viewModel: SearchViewModel by viewModels()
}
