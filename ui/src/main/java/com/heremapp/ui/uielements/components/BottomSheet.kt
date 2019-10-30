package com.heremapp.ui.uielements.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.DataBindingUtil.inflate
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.heremapp.presentation.extensions.onChanged
import com.heremapp.presentation.search.BottomSheetViewModel
import com.heremapp.presentation.search.BottomSheetViewModel.Companion.DISPLAY_STATE_FULL
import com.heremapp.presentation.search.BottomSheetViewModel.Companion.DISPLAY_STATE_HIDDEN
import com.heremapp.presentation.search.BottomSheetViewModel.Companion.DISPLAY_STATE_PEEK
import com.heremapp.presentation.search.BottomSheetViewModel.Companion.DisplayState
import com.heremapp.ui.R
import com.heremapp.ui.databinding.ViewBottomSheetBinding
import com.heremapp.ui.uielements.listeners.BottomSheetListener
import com.heremapp.ui.uielements.listeners.OnSwipeFromBottomGestureDetector
import com.heremapp.ui.uielements.listeners.OnSwipeGestureDetector
import com.heremapp.ui.uielements.listeners.OnSwipeGestureDetector.Companion.SWIPE_DOWN
import com.heremapp.ui.uielements.listeners.OnSwipeGestureDetector.Companion.SWIPE_UP
import com.heremapp.ui.uielements.listeners.OnSwipeGestureDetector.Companion.SWIPE_UP_FROM_BOTTOM
import com.heremapp.ui.uielements.listeners.OnSwipeGestureDetector.Companion.SwipeDirection
import com.heremapp.utility.messaging.MessageHandler.Companion.log

/**
 * BottomSheet view, monitors related swipe gestures and viewmodel's display state and updates behavior accordingly.
 */
class BottomSheet @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr),
    BottomSheetListener {

    // TODO: Improve gesture detector implementations.
    override val swipeDetector: OnSwipeGestureDetector by lazy { OnSwipeGestureDetector() }
    override val bottomSwipeDetector: OnSwipeFromBottomGestureDetector by lazy { OnSwipeFromBottomGestureDetector() }

    lateinit var viewModel: BottomSheetViewModel

    val binding: ViewBottomSheetBinding = inflate(LayoutInflater.from(context), R.layout.view_bottom_sheet, this, true)

    private val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
    private val gestureDetector: GestureDetectorCompat by lazy { GestureDetectorCompat(context, swipeDetector) }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else {
            super.dispatchTouchEvent(event)
        }
    }

    override fun requestDisplay(): Boolean {
        return viewModel.requestDisplayBottomSheet()
    }

    override fun requestDismiss(): Boolean {
        return viewModel.requestDismissBottomSheet()
    }

    @SuppressLint("CheckResult")
    fun setDependencies(viewModel: BottomSheetViewModel) {
        // Set view model & related listeners
        this.viewModel = viewModel
        this.viewModel.displayState.onChanged(::updateBottomSheetBehavior)

        initBottomSheetBehavior()

        // Set swipe detector and related listeners
        swipeDetector.subscribeToSwipeEvents()
            .subscribe(::handleSwipeEvent, ::log)
        bottomSwipeDetector.subscribeToSwipeEvents()
            .subscribe(::handleSwipeEvent, ::log)
    }

    /**
     * Notify viewModel of swipe events
     *
     * TODO: Implement viewmodel methods such that view logic is removed
     */
    private fun handleSwipeEvent(@SwipeDirection direction: Int) {
        when (direction) {
            SWIPE_UP -> viewModel.displayState.set(DISPLAY_STATE_FULL)
            SWIPE_DOWN -> viewModel.displayState.set(DISPLAY_STATE_PEEK)
            SWIPE_UP_FROM_BOTTOM -> viewModel.requestDisplayBottomSheet()
            else -> { /* No op */ }
        }
    }

    /**
     * Programmatically Update the bottom sheet display.
     */
    private fun updateBottomSheetBehavior(@DisplayState displayState: Int?) {
        when (displayState) {
            DISPLAY_STATE_HIDDEN -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            DISPLAY_STATE_PEEK -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            DISPLAY_STATE_FULL -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            else -> {
                log("No op, state: $displayState")
            }
        }
    }

    /**
     * Set initial behavior to hidden and a callback to viewmodel notifying behavior state changes.
     */
    private fun initBottomSheetBehavior() {
        // Set initial state to hidden
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        // Set callback to update the viewmodel of user driven state changes
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                /* No op */
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                log("onStateChanged, newState: $newState")

                mapBottomSheetBehaviorToDisplayState(newState)?.also(viewModel::updateDisplayState)
            }
        })
    }

    /**
     * Maps the bottom sheet behavior to a viewmodel display state.
     */
    private fun mapBottomSheetBehaviorToDisplayState(behavior: Int): @DisplayState Int? {
        return when (behavior) {
            BottomSheetBehavior.STATE_COLLAPSED -> DISPLAY_STATE_PEEK
            BottomSheetBehavior.STATE_EXPANDED -> DISPLAY_STATE_FULL
            else -> null
        }
    }
}