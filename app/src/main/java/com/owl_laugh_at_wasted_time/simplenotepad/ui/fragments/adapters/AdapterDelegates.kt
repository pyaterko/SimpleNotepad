package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.adapters

import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.elveum.elementadapter.SimpleBindingAdapter
import com.elveum.elementadapter.getColor
import com.elveum.elementadapter.simpleAdapter
import com.owl_laugh_at_wasted_time.domain.entity.*
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.*
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.getColorDrawable
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.preferences
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.toDateString
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
            itemFon.setBackgroundColor(item.color getColorDrawable context)
            textTitle.text = item.title
            checkBox.isChecked = item.done
            if (!format) {
                textViewNameDayOfWeek.text = toDateString(item.dateOfCreation)
                eventDayOfWeek.text = toDateString(item.data)
            } else {
                textViewNameDayOfWeek.text = item.dateOfCreation
                eventDayOfWeek.text = item.data
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

fun createEditNotesCategoryAdapter(listener: OnClickCategory) =
    simpleAdapter<ItemCategory, ItemCategoryBinding> {
        areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }

        bind { item ->
            if (item.id < 2) {
                vDelete.isVisible = false
            }
            if (item.name == "Все") {
                tvCategory.text = "без категории"
            } else {
                tvCategory.text = item.name
            }

        }
        listeners {
            root.onClick {
                listener.onClickCategoryItem(it)
            }
            vDelete.onClick {
                listener.deleteCategory(it)
            }
        }
    }

fun createListNotesCategoryAdapter(listener: OnClickCategory) =
    simpleAdapter<ItemCategory, ItemCategoryListNotesBinding> {
        areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
        areContentsSame = { oldItem, newItem -> oldItem == newItem }
        bind { item ->
            if (item.state) {
                tvListNotesCategory.setBackgroundColor(0xFF00245D.toInt())
                tvListNotesCategory.setTextColor(0xFFE2E2E2.toInt())

            } else {
                tvListNotesCategory.setBackgroundColor(0xFFE2E2E2.toInt())
                tvListNotesCategory.setTextColor(0xFF00245D.toInt())
            }
            tvListNotesCategory.text = item.name
        }
        listeners {
            root.onClick {
                listener.onClickCategoryItem(it)
            }
        }
    }

fun createNotesAdapter(context: Context, listener: OnNoteListener) =
    simpleAdapter<NotesListItem, ItemNoteBinding> {
        areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
        areContentsSame = { oldItem, newItem -> oldItem == newItem }
        bind { item ->
            val format = preferences(context).getBoolean(
                context.getString(R.string.settings_prettified_dates_key),
                false
            )
            imageViewMoreVert.isVisible =
                preferences(context).getBoolean(NotesListFragment.CURRENT_BOOLEAN, true)
            textViewDescription.isVisible =
                preferences(context).getBoolean(NotesListFragment.CURRENT_BOOLEAN, true)
            itemFon.setBackgroundColor(item.color getColorDrawable context)
            textViewTitle.text = item.title
            textViewDescription.text = item.text
            if (!format) {
                textViewNameDayOfWeek.text = toDateString(item.dateOfCreation)
            } else {
                textViewNameDayOfWeek.text = item.dateOfCreation
            }
            selectionIndicatorView.background = if (item.isChecked)
                ColorDrawable(getColor(R.color.half_black))
            else
                null

        }
        listeners {
            root.onClick {
                listener.launchToReadFragment(it)
            }
            root.onLongClick {
                listener.toggleSelection(it)
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
            Glide.with(ivPhoto)
                .load("https://loremflickr.com/320/240/${item.text}")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.macronutrientes)
                .error(R.drawable.macronutrientes)
                .into(ivPhoto)
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
    fun launchToReadFragment(itemNote: NotesListItem)
    fun toggleSelection(itemNote: NotesListItem)
    fun showMenu(view: View, itemNote: NotesListItem)
}

interface OnClickCategory {
    fun onClickCategoryItem(item: ItemCategory)
    fun deleteCategory(item: ItemCategory)

}