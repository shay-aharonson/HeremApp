package com.heremapp.ui.uielements.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IntDef

/**
 * Displays a circle.
 */
class CircleView(
    context: Context?,
    attrs: AttributeSet?,
    colour: Int,
    @FillStyle rippleType: Int,
    private val rippleStrokeWidth: Float
) : View(context, attrs)     {

    companion object {
        const val FILL = 1
        const val STROKE = 2
        const val FILL_AND_STROKE = 3

        @IntDef (
            FILL,
            STROKE,
            FILL_AND_STROKE
        ) annotation class FillStyle
    }

    private val paint = Paint()

    init {
        if (context == null) throw IllegalArgumentException("Context is null.")
        if (attrs == null) throw IllegalArgumentException("Attribute set is null.")

        visibility = View.INVISIBLE

        paint.apply {
            isAntiAlias = true
            color = colour
            style = when (rippleType) {
                FILL -> {
                    strokeWidth = 0f
                    Paint.Style.FILL
                }
                STROKE -> {
                    strokeWidth = rippleStrokeWidth
                    Paint.Style.STROKE
                }
                FILL_AND_STROKE -> {
                    strokeWidth = rippleStrokeWidth
                    Paint.Style.FILL_AND_STROKE
                }
                else -> throw IllegalArgumentException("Unknown fill style: $rippleType.")
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val radius: Float = Math.min(width, height) / 2.toFloat()
        canvas?.drawCircle(radius, radius, radius - rippleStrokeWidth, paint)
    }
}