package com.owl_laugh_at_wasted_time.simplenotepad.ui.base.callback

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.elveum.elementadapter.SimpleBindingAdapter
import com.elveum.elementadapter.dsl.BindingHolder
import com.owl_laugh_at_wasted_time.simplenotepad.R
import kotlin.math.absoluteValue

class TouchHelperCallback<T>(
    private val adapter: SimpleBindingAdapter<T>,
    private val swipeListener: (T) -> Unit
) : ItemTouchHelper.Callback() {
    private val bgRect = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val iconBounds = Rect()

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (viewHolder is BindingHolder) {
            makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.START)
        } else {
            makeFlag(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.START)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        swipeListener.invoke(adapter.currentList[viewHolder.adapterPosition])
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE && viewHolder is ItemTouchViewHolder) {
            viewHolder.onItemSelected(viewHolder)
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is ItemTouchViewHolder) viewHolder.onItemCleared()
        super.clearView(recyclerView, viewHolder)
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        var newDx = dX
        if (newDx <= -150f) {
            newDx = -150f
        }
        val itemView = viewHolder.itemView

        if (dX < 0) {
            drawBackground(canvas, itemView, newDx)
            drawIcon(canvas, itemView, newDx)
        }
        super.onChildDraw(
            canvas,
            recyclerView,
            viewHolder,
            newDx,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    private fun drawIcon(canvas: Canvas, itemView: View, dX: Float) {
        val icon = itemView.resources.getDrawable(R.drawable.ic_delete, itemView.context.theme)
        val iconSizeVert = itemView.resources.getDimensionPixelSize(R.dimen.icon_size_vert)
        val iconSizeHor = itemView.resources.getDimensionPixelSize(R.dimen.icon_size_hor)

        val marginVert = (itemView.bottom - itemView.top - iconSizeVert) / 2
        val newDx = dX.absoluteValue
        with(iconBounds) {
            left = itemView.right - newDx.toInt() / 2
            top = itemView.top + marginVert
            right = left + iconSizeHor
            bottom = top + iconSizeVert
        }

        icon.bounds = iconBounds
        icon.draw(canvas)
    }

    private fun drawBackground(canvas: Canvas, itemView: View, dX: Float) {
        with(bgRect) {
            left = itemView.right.toFloat() + dX
            top = itemView.top.toFloat()
            right = itemView.right.toFloat() - dX / 3
            bottom = itemView.bottom.toFloat()
        }

        with(paint) {
            color = itemView.resources.getColor(R.color.color_red, itemView.context.theme)
        }

        canvas.drawRect(bgRect, paint)

    }
}

interface ItemTouchViewHolder {

    fun onItemSelected(viewHolder: RecyclerView.ViewHolder)

    fun onItemCleared()
}

