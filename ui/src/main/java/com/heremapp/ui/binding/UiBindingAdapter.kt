package com.heremapp.ui.binding

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.heremapp.ui.R

/**
 * Loads image from a file into ImageView.
 */
@BindingAdapter("imagePath")
fun ImageView.loadImageFromFile(path: String?) {
    if (!path.isNullOrBlank()) Glide.with(this)
        .setDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.drawable.ic_image_placeholder)
                .format(DecodeFormat.PREFER_RGB_565)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
        )
        .load(path)
        .thumbnail(0.3f)
        .into(this)
}

/**
 * Sets the view's visibility attribute to either GONE or VISIBLE based on the given boolean value.
 */
@BindingAdapter("isVisible")
fun View.setIsVisible(isVisible: Boolean) {
    if (isVisible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}