package com.heremapp.ui.uielements.listeners

import android.content.res.Resources
import android.view.MotionEvent
import com.heremapp.utility.messaging.MessageHandler.Companion.log

/**
 * Gesture detector listener that identifies swipe gestures from the bottom of the screen.
 */
class OnSwipeFromBottomGestureDetector : OnSwipeGestureDetector() {

    companion object {
        const val BOTTOM_SWIPE_THRESHOLD = 250
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        return isSwipeFromBottom(e1, e2)
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        return isSwipeFromBottom(e1, e2)
    }

    private fun isSwipeFromBottom(e1: MotionEvent, e2: MotionEvent): Boolean {
        val x1 = e1.rawX
        val y1 = e1.rawY
        val x2 = e2.rawX
        val y2 = e2.rawY

        return if (getDirection(x1, y1, x2, y2) == SWIPE_UP && isFromBottom(y1)) {
            log("Swipe from bottom.")
            swipeSubject.onNext(SWIPE_UP_FROM_BOTTOM)
            true
        } else
            false
    }

    private fun isFromBottom(initialY: Float): Boolean {
        log("Screen Height[${Resources.getSystem().displayMetrics.heightPixels}], y1[$initialY]")
        return initialY > Resources.getSystem().displayMetrics.heightPixels - BOTTOM_SWIPE_THRESHOLD
    }
}