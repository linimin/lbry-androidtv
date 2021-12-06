package app.newproj.lbrytv.viewmodel

import androidx.leanback.widget.Row
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.newproj.lbrytv.data.dto.WalletBalance
import app.newproj.lbrytv.data.repo.HomeRowRepository
import app.newproj.lbrytv.data.repo.WalletRepository
import app.newproj.lbrytv.util.mapRow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    homeRowRepo: HomeRowRepository,
    walletRepository: WalletRepository,
) : ViewModel() {

    private val _walletBalance = MutableStateFlow<String>("Initializing …")
    val totalWalletBalance: StateFlow<String> = _walletBalance

    val rows: Flow<PagingData<Row>> = homeRowRepo
        .rowModels()
        .mapRow()
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            while (isActive) {
                val walletBalance = walletRepository.walletBalance()
                _walletBalance.value =
                    shortCurrencyFormat(BigDecimal(walletBalance.total).toDouble())
                delay(5000)
            }
        }
    }

    private fun shortCurrencyFormat(value: Double): String {
        val format = DecimalFormat("#,###.#")
        if (value > 1000000000.00) {
            return String.format("%sB", format.format(value / 1000000000.0))
        }
        if (value > 1000000.0) {
            return String.format("%sM", format.format(value / 1000000.0))
        }
        if (value > 1000.0) {
            return String.format("%sK", format.format(value / 1000.0))
        }
        if (value == 0.0) {
            return "0"
        }
        return format.format(value)
    }
}
