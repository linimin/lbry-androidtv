package app.newproj.lbrytv.bindingadapter

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("imageUri")
fun loadImage(imageView: ImageView, uri: Uri?) {
    Glide.with(imageView)
            .load(uri)
            .into(imageView)
}
