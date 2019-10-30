package com.heremapp.ui.uielements.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.BindingAdapter
import com.heremapp.presentation.base.LoadingViewModel
import com.heremapp.ui.databinding.ViewProgressLoadingBinding

/**
 * Loading view with a centered progress bar. Will display according to [LoadingViewModel.isLoading].
 */
class LoadingProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    lateinit var viewModel: LoadingViewModel

    val binding = ViewProgressLoadingBinding.inflate(LayoutInflater.from(context), this, true)
}

@BindingAdapter("viewModel")
fun LoadingProgressView.setViewModel(viewModel: LoadingViewModel) {
    this.binding.viewModel = viewModel
}