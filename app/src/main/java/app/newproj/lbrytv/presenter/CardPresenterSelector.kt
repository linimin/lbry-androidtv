package app.newproj.lbrytv.presenter

import androidx.leanback.widget.Presenter
import androidx.leanback.widget.PresenterSelector
import app.newproj.lbrytv.data.dto.CardPresentable
import app.newproj.lbrytv.data.dto.ClaimCard
import app.newproj.lbrytv.data.dto.ClaimType
import app.newproj.lbrytv.data.dto.SettingCard

class CardPresenterSelector : PresenterSelector() {
    override fun getPresenter(item: Any): Presenter {
        require(item is CardPresentable)
        return when (item) {
            is ClaimCard -> when (item.claim.valueType) {
                ClaimType.STREAM_CLAIM -> StreamCardPresenter()
                ClaimType.CHANNEL_CLAIM -> ChannelCardPresenter()
                null -> StreamCardPresenter()
            }
            is SettingCard -> SettingCardPresenter()
        }
    }
}
