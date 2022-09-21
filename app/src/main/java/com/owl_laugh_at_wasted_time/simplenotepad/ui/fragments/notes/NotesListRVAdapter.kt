package com.owl_laugh_at_wasted_time.notepad

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.owl_laugh_at_wasted_time.domain.entity.ItemNote
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.getColorDrawable
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.ItemNoteBinding


class NotesDiffCallBack(
    private val oldList: List<ItemNote>,
    private val newList: List<ItemNote>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]

}

class NotesListRVAdapter : RecyclerView.Adapter<NotesListRVAdapter.NoteViewHolder>() {

    var onNoteLongClickListener: ((ItemNote) -> Unit)? = null
    var onItemClickListener: ((ItemNote) -> Unit)? = null
    var onImageViewMoreVertListener: ((View) -> Unit)? = null

    var notes: List<ItemNote> = mutableListOf()
        set(value) {
            val result = DiffUtil.calculateDiff(NotesDiffCallBack(field, value))
            field = value
            result.dispatchUpdatesTo(this)
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNoteBinding.inflate(inflater, parent, false)
        return NoteViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)
        holder.itemView.tag = note
        with(holder.binding) {
            imageViewMoreVert.setOnClickListener {
                onImageViewMoreVertListener?.invoke(it)
            }
            root.setOnLongClickListener {
                onNoteLongClickListener?.invoke(note)
                return@setOnLongClickListener true
            }
            root.setOnClickListener {
                onItemClickListener?.invoke(note)
            }
        }


    }

    override fun getItemCount() = notes.size

    class NoteViewHolder(
        val binding: ItemNoteBinding,
        val context: Context,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: ItemNote) {
            with(binding) {
                imageViewMoreVert.tag = note
                itemFon.setBackgroundColor(note.color?.getColorDrawable(context))
                textViewTitle.text = note.title
                textViewDescription.text = note.text
                textViewNameDayOfWeek.text = note.dateOfCreation

            }
        }
    }

}