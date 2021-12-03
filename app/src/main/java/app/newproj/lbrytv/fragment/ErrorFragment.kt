package app.newproj.lbrytv.fragment

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.ErrorSupportFragment
import androidx.navigation.fragment.findNavController
import app.newproj.lbrytv.R
import app.newproj.lbrytv.viewmodel.ErrorViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ErrorFragment : ErrorSupportFragment() {
    private val viewModel: ErrorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        message = viewModel.message ?: getString(R.string.an_unknown_error_has_occurred)
        imageDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.error)
        buttonText = getString(R.string.close)
        setButtonClickListener {
            navigateBackToHome()
        }
    }

    private fun navigateBackToHome() {
        findNavController().popBackStack(R.id.homeFragment, false)
    }
}
