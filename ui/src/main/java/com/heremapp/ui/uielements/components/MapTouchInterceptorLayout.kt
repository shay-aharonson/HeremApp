package com.heremapp.ui.uielements.components

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.widget.FrameLayout

/**
 * A [FrameLayout] wrapper to intercept touch events, used to relay events related to the [BottomSheet] triggered by
 * fragments inflated in [MainActivity], especially [MapFragment].
 */
class MapTouchInterceptorLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    lateinit var scaleGestureListener: SimpleOnScaleGestureListener

    private val gestureDetector: ScaleGestureDetector by lazy { ScaleGestureDetector(context, scaleGestureListener) }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onInterceptTouchEvent(event)
    }
}