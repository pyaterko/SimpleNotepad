package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.todo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.getColorDrawable
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.preferences
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.toDateString
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.ItemTodoBinding

class ToDoDiffCallBack(
    private val oldList: List<ItemToDo>,
    private val newList: List<ItemToDo>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList == newList

}

class ToDoListRVAdapter : RecyclerView.Adapter<ToDoListRVAdapter.ToDoViewHolder>() {

    var onNoteLongClickListener: ((ItemToDo) -> Unit)? = null
    var onItemClickListener: ((ItemToDo) -> Unit)? = null
    var onImageViewMoreVertListener: ((View) -> Unit)? = null

    var list: List<ItemToDo> = mutableListOf()
        set(value) {
            val result = DiffUtil.calculateDiff(ToDoDiffCallBack(field, value))
            field = value
            result.dispatchUpdatesTo(this)
            //   notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTodoBinding.inflate(inflater, parent, false)
        return ToDoViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
        holder.itemView.tag = item
        with(holder.binding) {
            imageMoreVert.setOnClickListener {
                onImageViewMoreVertListener?.invoke(it)
            }
            root.setOnLongClickListener {
                onNoteLongClickListener?.invoke(item)
                return@setOnLongClickListener true
            }
            root.setOnClickListener {
                onItemClickListener?.invoke(item)
            }
        }
    }

    override fun getItemCount(): Int = list.size
    class ToDoViewHolder(
        val binding: ItemTodoBinding,
        val context: Context,
    ) : RecyclerView.ViewHolder(binding.root) {

        val format = preferences(context).getBoolean(
            context.getString(R.string.settings_prettified_dates_key),
            false
        )

        fun bind(item: ItemToDo) {
            with(binding) {
                imageMoreVert.tag = item
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
        }
    }
}