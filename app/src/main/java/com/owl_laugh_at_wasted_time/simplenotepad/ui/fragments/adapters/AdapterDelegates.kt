package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.adapters

import android.content.Context
import android.graphics.Paint
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.elveum.elementadapter.simpleAdapter
import com.owl_laugh_at_wasted_time.domain.entity.ItemNote
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.domain.entity.ShoppingListItem
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.getColorDrawable
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.getProductName
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.preferences
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.toDateString
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.ItemNoteBinding
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.ItemShoppingBinding
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.ItemTodoBinding


fun createToDoAdapter(context: Context, listener: OnToDoListener) =
    simpleAdapter<ItemToDo, ItemTodoBinding> {
        areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
        areContentsSame = { oldItem, newItem -> oldItem == newItem }
        bind { item ->
            val format = preferences(context).getBoolean(
                context.getString(R.string.settings_prettified_dates_key),
                false
            )
            itemFon.setBackgroundColor(item.color?.getColorDrawable(context))
            textTitle.text = item.title
            textDescription.text = item.text
            imageMoreVert.setBackgroundDrawable(
                item.priority.getColorDrawable(context)
            )
            if (!format) {
                textViewNameDayOfWeek.text = toDateString(item.dateOfCreation)
                iventDayOfWeek.text = toDateString(item.data)
            } else {
                textViewNameDayOfWeek.text = item.dateOfCreation
                iventDayOfWeek.text = item.data
            }

        }
        listeners {
            root.onClick {
                listener.launchToCreateToDoFragment(it)
            }
            imageMoreVert.onClick {
                listener.showMenu(imageMoreVert)
            }
        }
    }

fun createNotesAdapter(context: Context, listener: OnNoteListener) =
    simpleAdapter<ItemNote, ItemNoteBinding> {
        areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
        areContentsSame = { oldItem, newItem -> oldItem == newItem }
        bind { item ->
            val format = preferences(context).getBoolean(
                context.getString(R.string.settings_prettified_dates_key),
                false
            )
            itemFon.setBackgroundColor(item.color.getColorDrawable(context))
            textViewTitle.text = item.title
            textViewDescription.text = item.text
            if (!format) {
                textViewNameDayOfWeek.text = toDateString(item.dateOfCreation)
            } else {
                textViewNameDayOfWeek.text = item.dateOfCreation
            }

        }
        listeners {
            root.onClick {
                listener.launchToReadFragment(it)
            }
            root.onLongClick {
                listener.launchToCreateNotesFragment(it)
                return@onLongClick true
            }
            imageViewMoreVert.onClick {
                listener.showMenu(imageViewMoreVert, it)
            }
        }
    }

fun createSoppingAdapter(listener: OnShoppingListener) =
    simpleAdapter<ShoppingListItem, ItemShoppingBinding> {
        areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
        areContentsSame = { oldItem, newItem -> oldItem == newItem }
        bind { item ->
            textViewItemShoppingList.showStrikeThrough(item.done)
            Glide.with(ivFoto)
                .load("https://source.unsplash.com/random/300x300?${getProductName(item.text)}")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.macronutrientes)
                .error(R.drawable.macronutrientes)
                .into(ivFoto)
            textViewItemShoppingList.text = item.text
            checkBoxShoppingList.isChecked = item.done

        }
        listeners {
            checkBoxShoppingList.onClick { item ->
                listener.markAsPurchased(item)
            }
        }
    }

private fun TextView.showStrikeThrough(show: Boolean) {
    paintFlags =
        if (show) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
}


interface OnToDoListener {
    fun launchToCreateToDoFragment(itemToDo: ItemToDo)
    fun showMenu(view: View)

}

interface OnShoppingListener {
    fun markAsPurchased(item: ShoppingListItem)
}

interface OnNoteListener {
    fun launchToReadFragment(itemNote: ItemNote)
    fun launchToCreateNotesFragment(itemNote: ItemNote)
    fun showMenu(view: View, itemNote: ItemNote)
}