package app.newproj.lbrytv.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

fun DetailsSupportFragmentBackgroundController.loadImage(context: Context, uri: Uri?) {
    val displayMetrics = context.resources.displayMetrics
    this.coverDrawable
    Glide.with(context)
        .asBitmap()
        .load(uri)
        .override(displayMetrics.widthPixels, displayMetrics.heightPixels)
        .centerCrop()
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                coverBitmap = resource
                this@loadImage.coverDrawable.invalidateSelf()
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                coverBitmap = null
            }
        })
}
