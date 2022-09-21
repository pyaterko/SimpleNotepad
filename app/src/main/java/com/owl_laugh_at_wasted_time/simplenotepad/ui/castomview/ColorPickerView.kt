package com.owl_laugh_at_wasted_time.simplenotepad.ui.castomview

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import androidx.annotation.Dimension
import androidx.annotation.Dimension.DP
import com.owl_laugh_at_wasted_time.domain.entity.ItemColor
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.dip
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.getColorRes


class ColorPickerView : LinearLayout {

    companion object {
        private const val PALETTE_ANIMATION_DURATION = 150L
        private const val HEIGHT = "height"
        private const val SCALE = "scale"

        @Dimension(unit = DP)
        private const val COLOR_VIEW_PADDING = 8
    }

    var onColorClickListener: (ItemColor) -> Unit = { }

    val isOpen: Boolean
        get() = measuredHeight > 0

    private var desiredHeight = 180

    private val animator by lazy {
        ValueAnimator().apply {
            duration = PALETTE_ANIMATION_DURATION
            addUpdateListener(updateListener)
        }
    }

    private val updateListener by lazy {
        ValueAnimator.AnimatorUpdateListener { animator ->
            layoutParams.apply {
                height = animator.getAnimatedValue(HEIGHT) as Int
            }.let {
                layoutParams = it
            }

            val scaleFactor = animator.getAnimatedValue(SCALE) as Float
            for (i in 0 until childCount) {
                getChildAt(i).apply {
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                    alpha = scaleFactor
                }
            }
        }
    }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER

        ItemColor.values().forEach { color ->
            addView(
                ColorCircleView(context).apply {
                    fillColorRes = color.getColorRes()
                    tag = color
                    dip(COLOR_VIEW_PADDING).let {
                        setPadding(it.toInt(), it.toInt(), it.toInt(), it.toInt())
                    }
                    setOnClickListener {
                        onColorClickListener(it.tag as ItemColor)
                    }
                })
        }

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        layoutParams.apply {
            desiredHeight = height
            height = 0
        }.let {
            layoutParams = it
        }
    }

    fun open() {
        animator.cancel()
        animator.setValues(
            PropertyValuesHolder.ofInt(HEIGHT, measuredHeight, desiredHeight),
            PropertyValuesHolder.ofFloat(SCALE, getChildAt(0).scaleX, 1f)
        )
        animator.start()
    }

    fun close() {
        animator.cancel()
        animator.setValues(
            PropertyValuesHolder.ofInt(HEIGHT, measuredHeight, 0),
            PropertyValuesHolder.ofFloat(SCALE, getChildAt(0).scaleX, 0f)
        )
        animator.start()
    }

}