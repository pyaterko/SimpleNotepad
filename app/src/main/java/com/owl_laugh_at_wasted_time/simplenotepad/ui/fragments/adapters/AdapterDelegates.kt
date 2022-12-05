package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.adapters

import android.content.Context
import android.graphics.Paint
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.elveum.elementadapter.SimpleBindingAdapter
import com.elveum.elementadapter.simpleAdapter
import com.owl_laugh_at_wasted_time.domain.entity.ItemNote
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.domain.entity.ShoppingListItem
import com.owl_laugh_at_wasted_time.domain.entity.SubTaskItem
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.getColorDrawable
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.preferences
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.toDateString
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.ItemNoteBinding
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.ItemShoppingBinding
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.ItemSubtaskBinding
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.ItemTodoBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.notes.NotesListFragment

private fun createSubTaskAdapter(listener: OnToDoListener) =
    simpleAdapter<SubTaskItem, ItemSubtaskBinding> {
        areItemsSame = { oldItem, newItem -> oldItem.text == newItem.text }
        areContentsSame = { oldItem, newItem -> oldItem == newItem }
        bind { item ->
            tvSubtask.showStrikeThrough(item.done)
            tvSubtask.text = item.text
            chbSubtask.isChecked = item.done
        }
        listeners {
            chbSubtask.onClick {
                listener.markAsDone(it)
            }
            tvDelete.onClick {
                listener.deleteItem(it)
            }
        }
    }


fun createToDoAdapter(
    context: Context,
    listener: OnToDoListener
) =
    simpleAdapter<ItemToDo, ItemTodoBinding> {
        areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
        areContentsSame = { oldItem, newItem -> oldItem == newItem }
        bind { item ->
            val adapter = createSubTaskAdapter(listener)
            rvSubtaskList.layoutManager = LinearLayoutManager(context)
            rvSubtaskList.adapter = adapter
            listener.getSubTaskList(adapter, item, tvCount)
            val format = preferences(context).getBoolean(
                context.getString(R.string.settings_prettified_dates_key),
                false
            )
            itemFon.setBackgroundColor(item.color?.getColorDrawable(context))
            textTitle.text = item.title
            checkBox.isChecked = item.done
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
            buttonOpenRv.onClick {
                buttonOpenRv.animate().rotation(buttonOpenRv.rotation + 180f)
                listener.showSubTasks(rvSubtaskList)
            }
            checkBox.onClick {
                listener.markAsDoneToDo(it)
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
            imageViewMoreVert.isVisible =
                preferences(context).getBoolean(NotesListFragment.CURRENT_BOOLEAN, true)
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
                .load("https://loremflickr.com/320/240/${item.text}")
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
    fun markAsDoneToDo(itemToDo: ItemToDo)
    fun getSubTaskList(
        adapter: SimpleBindingAdapter<SubTaskItem>,
        itemToDo: ItemToDo,
        textView: TextView
    )

    fun showSubTasks(view: View)
    fun markAsDone(item: SubTaskItem)
    fun deleteItem(item: SubTaskItem)
}

interface OnShoppingListener {
    fun markAsPurchased(item: ShoppingListItem)
}

interface OnNoteListener {
    fun launchToReadFragment(itemNote: ItemNote)
    fun launchToCreateNotesFragment(itemNote: ItemNote)
    fun showMenu(view: View, itemNote: ItemNote)
}