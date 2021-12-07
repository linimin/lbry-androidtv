package app.newproj.lbrytv.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.leanback.widget.TitleView
import androidx.leanback.widget.TitleViewAdapter.BRANDING_VIEW_VISIBLE
import app.newproj.lbrytv.databinding.WalletBalanceTextViewBinding

class LbcTitleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TitleView(context, attrs) {

    private val walletBalanceTextViewBinding =
        WalletBalanceTextViewBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

    fun setWalletBalance(balance: String) {
        walletBalanceTextViewBinding.walletBalanceTextView.text = balance
    }

    override fun updateComponentsVisibility(flags: Int) {
        super.updateComponentsVisibility(flags)
        walletBalanceTextViewBinding.root.isVisible =
            flags and BRANDING_VIEW_VISIBLE == BRANDING_VIEW_VISIBLE
    }
}
