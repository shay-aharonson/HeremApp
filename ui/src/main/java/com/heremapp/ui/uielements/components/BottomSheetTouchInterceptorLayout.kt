package com.heremapp.ui.uielements.components

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.view.GestureDetectorCompat
import com.heremapp.ui.uielements.listeners.BottomSheetListener

/**
 * A [FrameLayout] wrapper to intercept touch events, used to relay events related to the [BottomSheet] triggered by
 * fragments inflated in [MainActivity], especially [MapFragment].
 */
class BottomSheetTouchInterceptorLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    lateinit var bottomSheetListener: BottomSheetListener

    private val gestureDetector: GestureDetectorCompat by lazy { GestureDetectorCompat(context, bottomSheetListener.bottomSwipeDetector) }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        bottomSheetListener.requestDismiss()
        return gestureDetector.onTouchEvent(event)
    }
}