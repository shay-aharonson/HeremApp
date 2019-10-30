package com.heremapp.ui.uielements.components

import android.animation.AnimatorSet
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.BindingAdapter
import com.heremapp.presentation.base.LoadingViewModel
import com.heremapp.presentation.extensions.onChanged
import com.heremapp.ui.databinding.ViewRippleLoadingBinding
import com.heremapp.utility.messaging.MessageHandler.Companion.log

/**
 * Base implementation of a loading view which generates ripples while loading.
 */
open class LoadingRippleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    lateinit var viewModel: LoadingViewModel

    internal val binding = ViewRippleLoadingBinding.inflate(LayoutInflater.from(context), this, true)

    private val ripples = mutableListOf<AnimatorSet>()

    /**
     * Start the circle animations or creates a new pool of animating circle views if haven't done so already.
     */
    fun startRipples() {
        log("Starting ripple animations!")

        if (ripples.isEmpty()) {
            for (index in 1..5) {
                ripples.add(binding.rippleView.newRipple())
            }
        } else {
            ripples.forEach { it.start() }
        }
    }

    /**
     * Stop animations.
     */
    fun stopRipples() {
        log("Stopping ripple animations!")
        ripples.forEach { it.end() }
    }
}

/**
 * Set the loading viewmodel and listen to state changes to update the animations.
 */
@BindingAdapter("viewModel")
fun LoadingRippleView.setViewModel(viewModel: LoadingViewModel) {

    this.binding.viewModel = viewModel.apply {
        if (isLoading.get()) startRipples()

        isLoading.onChanged {
            if (it) startRipples() else stopRipples()
        }
    }
}