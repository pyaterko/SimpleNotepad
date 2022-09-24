package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.shopping

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.owl_laugh_at_wasted_time.domain.entity.ShoppingListItem
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.ItemShoppingListBinding

class ShoppingListDiffCallBack(
    private val oldItem: List<ShoppingListItem>,
    private val newItem: List<ShoppingListItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldItem.size

    override fun getNewListSize(): Int = newItem.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldItem[oldItemPosition].id == newItem[newItemPosition].id


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldItem[oldItemPosition] == newItem[newItemPosition]
}

class ShoppingListRVAdapter : RecyclerView.Adapter<ShoppingListRVAdapter.ShoppingListItemHolder>() {

    var onCheckBoxClickListener: ((View) -> Unit)? = null
    var onDeleteButtonClickListener: ((View) -> Unit)? = null

    var shoppingList: List<ShoppingListItem> = emptyList()
        set(value) {
            val diffCallBack = ShoppingListDiffCallBack(field, value)
            val diffResult = DiffUtil.calculateDiff(diffCallBack)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemShoppingListBinding.inflate(inflater, parent, false)
        return ShoppingListItemHolder(binding, parent.context)
    }


    override fun onBindViewHolder(holder: ShoppingListItemHolder, position: Int) {
        val shoppingListItem = shoppingList[position]
        holder.binding.textViewItemShoppingList.showStrikeThrough(shoppingListItem.done)
        holder.binding.checkBoxShoppingList.tag = shoppingListItem
        holder.binding.imageButtonShoppingList.tag = shoppingListItem

        holder.binding.checkBoxShoppingList.setOnClickListener {
            onCheckBoxClickListener?.invoke(it)
        }
        holder.binding.imageButtonShoppingList.setOnClickListener {
            onDeleteButtonClickListener?.invoke(it)
        }

        holder.binding.textViewItemShoppingList.text = shoppingListItem.text
        holder.binding.checkBoxShoppingList.isChecked = shoppingListItem.done
    }

    override fun getItemCount(): Int = shoppingList.size

    private fun TextView.showStrikeThrough(show: Boolean) {
        paintFlags =
            if (show) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }

    class ShoppingListItemHolder(
        val binding: ItemShoppingListBinding,
        val context: Context
    ) : RecyclerView.ViewHolder(binding.root)
}