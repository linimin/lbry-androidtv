package app.newproj.lbrytv.bindingadapter

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.leanback.widget.ImageCardView
import com.bumptech.glide.Glide

@BindingAdapter("imageUri")
fun loadImage(imageCardView: ImageCardView, uri: Uri?) {
    Glide.with(imageCardView)
        .load(uri)
        .into(imageCardView.mainImageView)
}

@BindingAdapter("mainImage")
fun loadDrawable(imageCardView: ImageCardView, @DrawableRes drawableRes: Int) {
    imageCardView.mainImage = ContextCompat.getDrawable(imageCardView.context, drawableRes)
}

@BindingAdapter("titleRes")
fun setTitleRes(imageCardView: ImageCardView, @StringRes titleRes: Int) {
    imageCardView.titleText = imageCardView.context.getString(titleRes)
}
