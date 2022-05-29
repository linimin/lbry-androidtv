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

package app.newproj.lbrytv.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BackgroundManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import app.newproj.lbrytv.R
import app.newproj.lbrytv.databinding.TvActivityBinding
import coil.imageLoader
import coil.request.ImageRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TvActivity : FragmentActivity() {
    private val viewModel: TvViewModel by viewModels()
    private lateinit var viewBinding: TvActivityBinding
    @Inject lateinit var backgroundManager: BackgroundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // `installSplashScreen()` must be called before calling super.onCreate(), see:
        // https://developer.android.com/guide/topics/ui/splash-screen/migrate#migrate_your_splash_screen_implementation
        installSplashScreen()
        super.onCreate(savedInstanceState)
        viewBinding = TvActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        backgroundManager.apply {
            attach(window)
            color = getColor(R.color.md_theme_dark_background)
        }
        lifecycleScope.launch {
            // https://developer.android.com/topic/libraries/architecture/coroutines#restart
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.wallpaperUrl.collectLatest(::loadWallpaper)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val options = NavOptions.Builder()
            .setPopUpTo(R.id.browseCategoriesFragment, inclusive = true, saveState = false)
            .build()
        findNavController(R.id.nav_host_fragment)
            .navigate(R.id.browseCategoriesFragment, null, options)
    }

    private fun loadWallpaper(url: String) = imageLoader.enqueue(
        ImageRequest.Builder(this)
            .data(url)
            .target { backgroundManager.drawable = it }
            .build()
    )
}
