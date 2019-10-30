package com.heremapp.ui.uielements.components

import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import com.heremapp.presentation.communication.CommunicationSupervisorViewModel.Companion.CommunicationStatus
import com.heremapp.presentation.communication.CommunicationSupervisorViewModel.Companion.STATUS_NONE
import com.heremapp.presentation.communication.CommunicationSupervisorViewModel.Companion.STATUS_NO_INTERNET
import com.heremapp.presentation.communication.CommunicationSupervisorViewModel.Companion.STATUS_NO_LOCATION
import com.heremapp.ui.R

/**
 * Displays the communication state with loading ripples when either no wifi or no GPS signal.
 */
class CommunicationRippleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LoadingRippleView(context, attrs, defStyleAttr) {

    internal val communicationStateToResourceMap = mapOf(
                STATUS_NO_INTERNET to R.drawable.ic_cell_tower,
                STATUS_NO_LOCATION to R.drawable.ic_satellite,
                STATUS_NONE to R.drawable.ic_cell_and_satellite
    )
}

@BindingAdapter("communicationState")
fun CommunicationRippleView.setCommunicationState(@CommunicationStatus state: Int) {
    this.communicationStateToResourceMap[state]?.let { imageRes ->
        binding.loadingRippleImage.setImageResource(imageRes)
    }
}