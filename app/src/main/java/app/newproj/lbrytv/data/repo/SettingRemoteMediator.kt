package app.newproj.lbrytv.data.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.MainDatabase
import app.newproj.lbrytv.data.dto.SettingType
import app.newproj.lbrytv.data.entity.Setting
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class SettingRemoteMediator @Inject constructor(
    private val db: MainDatabase,
): RemoteMediator<Int, Setting>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Setting>
    ): MediatorResult {
        if (loadType == LoadType.REFRESH) {
            db.settingDao().clear()
            val user = db.userDao().user()
            if (user?.hasVerifiedEmail == true) {
                db.settingDao()
                    .insert(
                        Setting(
                            title = R.string.sign_out,
                            type = SettingType.SIGN_OUT,
                            icon = R.drawable.account
                        )
                    )
            } else {
                db.settingDao()
                    .insert(
                        Setting(
                            title = R.string.sign_in,
                            type = SettingType.SIGN_IN,
                            icon = R.drawable.account
                        )
                    )
            }
        }
        return MediatorResult.Success(endOfPaginationReached = true)
    }
}
