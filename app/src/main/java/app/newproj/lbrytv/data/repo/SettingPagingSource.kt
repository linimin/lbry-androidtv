package app.newproj.lbrytv.data.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.dto.SettingType
import app.newproj.lbrytv.data.entity.Setting
import javax.inject.Inject

class SettingPagingSource @Inject constructor(
    private val userRepo: UserRepository,
) : PagingSource<Int, Setting>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Setting> {
        val user = userRepo.user()
        val settings = mutableListOf<Setting>()
        if (user.hasVerifiedEmail == true) {
            settings.add(
                Setting(
                    title = R.string.sign_out,
                    type = SettingType.SIGN_OUT,
                    icon = R.drawable.account
                )
            )
        } else {
            settings.add(
                Setting(
                    title = R.string.sign_in,
                    type = SettingType.SIGN_IN,
                    icon = R.drawable.account
                )
            )
        }
        return LoadResult.Page(settings, null, null)
    }

    override fun getRefreshKey(state: PagingState<Int, Setting>): Int? = null
}
