package app.newproj.lbrytv.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.comparator.CardPresentableComparator
import app.newproj.lbrytv.data.dto.CardPresentable
import app.newproj.lbrytv.data.dto.RelatedClaim
import app.newproj.lbrytv.presenter.CardPresenterSelector
import app.newproj.lbrytv.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {
    private val viewModel: SearchViewModel by viewModels()
    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
    private val searchResultAdapter =
        PagingDataAdapter(CardPresenterSelector(), CardPresentableComparator)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val header = HeaderItem(getString(R.string.search_result))
        val searchResultRow = ListRow(header, searchResultAdapter)
        rowsAdapter.add(searchResultRow)
        setSearchResultProvider(this)
        setOnItemViewClickedListener { _, item, _, _ ->
            when (val card = item as? CardPresentable) {
                is RelatedClaim -> onClickedVideo(card)
                else -> Unit
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResult.collectLatest(searchResultAdapter::submitData)
        }
    }

    override fun getResultsAdapter(): ObjectAdapter {
        return rowsAdapter
    }

    override fun onQueryTextChange(newQuery: String?): Boolean {
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        viewModel.search(query)
        return true
    }

    private fun onClickedVideo(claim: RelatedClaim) {
        SearchFragmentDirections
            .actionSearchFragmentToVideoPlayerFragment(claim.id)
            .let(findNavController()::navigate)
    }
}
