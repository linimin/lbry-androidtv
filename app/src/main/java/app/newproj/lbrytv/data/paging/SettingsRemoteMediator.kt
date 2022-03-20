/*
 * MIT License
 *
 * Copyright (c) 2022 LIN I MIN
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package app.newproj.lbrytv.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import app.newproj.lbrytv.R
import app.newproj.lbrytv.data.AppDatabase
import app.newproj.lbrytv.data.entity.Setting
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class SettingRemoteMediator @Inject constructor(
    private val db: AppDatabase,
) : RemoteMediator<Int, Setting>() {
    private val settingDao = db.settingDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Setting>,
    ): MediatorResult {
        if (loadType == LoadType.REFRESH) {
            db.withTransaction {
                settingDao.deleteAll()
                settingDao.upsert(
                    listOf(
                        Setting(
                            id = R.string.switch_account,
                            titleRes = R.string.switch_account,
                            iconRes = R.drawable.person_add,
                            enabled = true
                        )
                    )
                )
            }
        }
        return MediatorResult.Success(endOfPaginationReached = true)
    }
}
