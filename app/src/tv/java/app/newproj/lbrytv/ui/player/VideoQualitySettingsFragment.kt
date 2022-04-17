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

package app.newproj.lbrytv.ui.player

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.preference.LeanbackPreferenceFragmentCompat
import androidx.leanback.preference.LeanbackSettingsFragmentCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import app.newproj.lbrytv.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

const val VIDEO_QUALITY_AUTO_SIZE = -1

@AndroidEntryPoint
class VideoQualitySettingsFragment : LeanbackSettingsFragmentCompat() {
    @Inject lateinit var preferenceFragmentProvider: Provider<VideoQualityPreferenceFragment>

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat?,
        pref: Preference?,
    ) = true

    override fun onPreferenceStartScreen(
        caller: PreferenceFragmentCompat?,
        pref: PreferenceScreen?,
    ) = true

    override fun onPreferenceStartInitialScreen() {
        startPreferenceFragment(preferenceFragmentProvider.get())
    }
}

@AndroidEntryPoint
class VideoQualityPreferenceFragment @Inject constructor() : LeanbackPreferenceFragmentCompat() {
    private val viewModel: VideoQualitySettingsViewModel by viewModels({ requireParentFragment() })
    private val navController: NavController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect {
                preferenceScreen.removeAll()
                preferenceScreen.addPreference(
                    ListPreference(preferenceManager.context).apply {
                        key = R.id.video_quality_settings.toString()
                        title = getString(R.string.video_quality)
                        entries =
                            (listOf(getString(R.string.auto)) + it.qualityOptions.map { it.name })
                                .toTypedArray()
                        entryValues =
                            (listOf(VIDEO_QUALITY_AUTO_SIZE.toString())
                                    + it.qualityOptions.map { it.size.toString() })
                                .toTypedArray()
                        setOnPreferenceChangeListener { _, _ ->
                            navController.popBackStack()
                            true
                        }
                        summaryProvider = Preference.SummaryProvider<ListPreference> { preference ->
                            it.qualityOptions
                                .find { it.size.toString() == preference.value }
                                ?.name
                                ?: getString(R.string.auto)
                        }
                    }
                )
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceScreen = preferenceManager
            .createPreferenceScreen(preferenceManager.context)
            .apply {
                title = getString(R.string.settings)
            }
    }
}
