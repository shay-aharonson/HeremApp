package com.heremapp.ui.uielements.listeners

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.annotation.IntDef
import com.heremapp.utility.messaging.MessageHandler.Companion.log
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlin.math.abs

/**
 * Gesture detector listener that identifies swipe gestures and their direction.
 */
typealias FlingData = Pair<Float, Float>
open class OnSwipeGestureDetector : GestureDetector.SimpleOnGestureListener() {

    internal val swipeSubject = PublishSubject.create<@SwipeDirection Int>()

    companion object {
        const val SWIPE_MIN_DISTANCE = 120
        const val SWIPE_MIN_VELOCITY = 5000

        const val SWIPE_UP = 1
        const val SWIPE_DOWN = 2
        const val SWIPE_LEFT = 3
        const val SWIPE_RIGHT = 4
        const val SWIPE_UP_FROM_BOTTOM = 5

        @IntDef(
            SWIPE_UP,
            SWIPE_DOWN,
            SWIPE_LEFT,
            SWIPE_RIGHT,
            SWIPE_UP_FROM_BOTTOM
        )
        annotation class SwipeDirection
    }

    /**
     * Grab two events located on the plane at e1=(x1, y1) and e2=(x2, y2)
     * Let e1 be the initial event
     * e2 can be located at 4 different positions, consider the following diagram
     * (Assume that lines are separated by 90 degrees.)
     *
     *   \ A  /
     *    \  /
     * D   e1   B
     *    /  \
     *   / C  \
     *
     * So if (x2,y2) falls in region:
     * A => it's an UP swipe
     * B => it's a RIGHT swipe
     * C => it's a DOWN swipe
     * D => it's a LEFT swipe
     */
    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        val x1 = e1.rawX
        val y1 = e1.rawY
        val x2 = e2.rawX
        val y2 = e2.rawY

        getDirection(x1, y1, x2, y2).let { direction ->

            when (direction) {
                SWIPE_LEFT, SWIPE_RIGHT -> FlingData(
                    abs(x2 - x1).also { log(it.toString()) },
                    abs(velocityX).also { log(it.toString()) })
                SWIPE_UP, SWIPE_DOWN -> FlingData(
                    abs(y2 - y1).also { log(it.toString()) },
                    abs(velocityY).also { log(it.toString()) })
                else -> null
            }?.also { flingData ->
                return if (isSwipe(flingData)) {
                    log("Swipe event: $direction")
                    swipeSubject.onNext(direction)
                    true
                } else {
                    false
                }
            }
        }
        return false
    }

    fun subscribeToSwipeEvents(): Observable<Int> {
        return swipeSubject
    }

    internal fun isSwipe(data: FlingData): Boolean {
        return data.first >= SWIPE_MIN_DISTANCE && data.second >= SWIPE_MIN_VELOCITY
    }

    /**
     * Given two points in the plane p1=(x1, x2) and p2=(y1, y1), this method
     * returns the direction that an arrow pointing from p1 to p2 would have.
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return the direction
     */
    internal fun getDirection(x1: Float, y1: Float, x2: Float, y2: Float): Int {
        return directionFromAngle(getAngle(x1, y1, x2, y2))
    }

    /**
     * Finds the angle between two points in the plane (x1,y1) and (x2, y2)
     * The angle is measured with 0/360 being the X-axis to the right, angles
     * increase counter clockwise.
     *
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return the angle between two points
     */
    internal fun getAngle(x1: Float, y1: Float, x2: Float, y2: Float): Double {
        val rad = Math.atan2((y1 - y2).toDouble(), (x2 - x1).toDouble()) + Math.PI
        return (rad * 180 / Math.PI + 180) % 360;
    }

    /**
     * Returns a direction given an angle.
     * Directions are defined as follows:
     *
     * Up: [45, 135]
     * Right: [0,45] and [315, 360]
     * Down: [225, 315]
     * Left: [135, 225]
     *
     * @param angle an angle from 0 to 360 - e
     * @return the direction of an angle
     */
    internal fun directionFromAngle(angle: Double): Int {
        return when {
            inRange(angle, 45f, 135f) -> SWIPE_UP
            inRange(angle, 0f, 45f) || inRange(angle, 315f, 360f) -> SWIPE_RIGHT
            inRange(angle, 225f, 315f) -> SWIPE_DOWN
            else -> SWIPE_LEFT
        }
    }

    /**
     * @param angle an angle
     * @param init the initial bound
     * @param end the final bound
     * @return returns true if the given angle is in the interval [init, end).
     */
    internal fun inRange(angle: Double, init: Float, end: Float): Boolean {
        return (angle >= init) && (angle < end)
    }
}