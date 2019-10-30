package com.heremapp.ui.uielements.components

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.RESTART
import android.content.Context
import android.util.AttributeSet
import android.util.Property
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.heremapp.ui.R
import com.heremapp.ui.uielements.components.CircleView.Companion.FILL
import com.heremapp.ui.uielements.components.CircleView.Companion.FillStyle
import java.util.concurrent.ThreadLocalRandom

/**
 * View that generates circle views based on the given attributes to enable a ripple effect.
 *
 * TODO: Refactor implementation and usage.
 */
class RippleView(context: Context?, private val attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    companion object {
        private const val DEFAULT_RIPPLE_SCALE = 5f
        private const val DEFAULT_FILL_STYLE = FILL
        private const val MIN_RIPPLE_DURATION = 400
        private const val MAX_RIPPLE_DURATION = 1000
        private const val NO_RIPPLE_DURATION = -1
    }

    @FillStyle
    private var fillStyle: Int = DEFAULT_FILL_STYLE
    private var rippleScale: Float = DEFAULT_RIPPLE_SCALE
    private var rippleColor: Int = 0
    private var rippleStrokeWidth: Float = 0f
    private var rippleRadius: Float = 0f
    private var duration: Int = NO_RIPPLE_DURATION

    init {
        if (context == null) throw IllegalArgumentException("Context is null.")
        if (attrs == null) throw IllegalArgumentException("Attribute set is null.")

//        val metrics = Resources.getSystem().displayMetrics
//        val defaultRadius = max(image?.measuredWidth ?: 0, image?.measuredHeight ?: 0)
        val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.RippleView)
        rippleRadius = styledAttributes.getDimension(R.styleable.RippleView_rv_radius, 0f)
//            TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP,
//            defaultRadius.toFloat(),
//            metrics))
        rippleStrokeWidth = styledAttributes.getDimension(R.styleable.RippleView_rv_strokeWidth, resources.getDimension(R.dimen.ripple_stroke_width))
        rippleColor = styledAttributes.getColor(R.styleable.RippleView_rv_color, ContextCompat.getColor(context, R.color.colorAccent))
        fillStyle = styledAttributes.getInt(R.styleable.RippleView_rv_type, DEFAULT_FILL_STYLE)
        rippleScale = styledAttributes.getFloat(R.styleable.RippleView_rv_scale, DEFAULT_RIPPLE_SCALE)
        duration = styledAttributes.getInteger(R.styleable.RippleView_rv_duration, NO_RIPPLE_DURATION)
        styledAttributes.recycle()
    }

    /**
    * Call this class to initiate a new ripple animation.
    */
    fun newRipple(): AnimatorSet {
        val circleView = CircleView(context, attrs, rippleColor, fillStyle, rippleStrokeWidth).apply {
            visibility = View.VISIBLE
        }

        addView(circleView, getCircleViewLayoutParams())

        val animationDuration = if (duration == NO_RIPPLE_DURATION) {
            randomAnimationDuration()
        } else duration

        return generateRipple(duration = animationDuration, target = circleView).apply {
            start()
        }
    }

    private fun generateRipple(duration: Int, target: CircleView): AnimatorSet {
        val animatorList = ArrayList<Animator>()

        animatorList.add(provideAnimator(
            target = target,
            type = View.SCALE_X,
            animDuration = duration,
            scale = rippleScale
        ))

        animatorList.add(provideAnimator(
            target = target,
            type = View.SCALE_Y,
            animDuration = duration,
            scale = rippleScale
        ))

        animatorList.add(provideAnimator(
            target = target,
            type = View.ALPHA,
            animDuration = duration
        ))

        return AnimatorSet().apply {
            playTogether(animatorList)
        }
    }

    private fun provideAnimator(
        target: View,
        type: Property<View, Float>,
        animDuration: Int,
        scale: Float = DEFAULT_RIPPLE_SCALE
    ): ObjectAnimator {
        val scaleAmount = if (type == View.ALPHA) 0f else scale

        return ObjectAnimator.ofFloat(target, type, scaleAmount).apply {
            repeatCount = INFINITE
            repeatMode = RESTART
            duration = animDuration.toLong()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator) {
//                    removeView(target)
                }
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
            })
        }
    }

    private fun getCircleViewLayoutParams(): LayoutParams {
        val widthHeight = (2 * rippleRadius + rippleStrokeWidth).toInt()
        return RelativeLayout.LayoutParams(widthHeight, widthHeight).apply {
            addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        }
    }

    private fun randomAnimationDuration() = ThreadLocalRandom.current().nextInt(MIN_RIPPLE_DURATION, MAX_RIPPLE_DURATION)
}