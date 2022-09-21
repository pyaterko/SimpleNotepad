package com.owl_laugh_at_wasted_time.simplenotepad.ui.base.behaviors

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FabBehavior(context: Context, attributeSet: AttributeSet) :
    FloatingActionButton.Behavior(context, attributeSet) {

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {

        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(
            coordinatorLayout,
            child, directTargetChild, target, axes, type
        )
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
//           // вниз
//        if (dyConsumed > 0) {
//            val layoutParams = child.layoutParams as CoordinatorLayout.LayoutParams
//            val fab_bottomMargin = layoutParams.bottomMargin
//            child.animate().translationY((child.height + fab_bottomMargin).toFloat())
//                .setInterpolator(LinearInterpolator()).start()
//        } else if (dyConsumed < 0) {
//            child.animate().translationY(0F).setInterpolator(LinearInterpolator()).start()
//        }

              // исчезает
        if (dyConsumed > 0 && child.visibility == View.VISIBLE) {
            child.hide(object : FloatingActionButton.OnVisibilityChangedListener() {

                @SuppressLint("RestrictedApi")
                override fun onHidden(fab: FloatingActionButton) {
                    fab.visibility = View.INVISIBLE
                }
            })
        } else if (dyConsumed < 0 && child.visibility != View.VISIBLE) {
            child.show()
        }

        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )
    }

}

