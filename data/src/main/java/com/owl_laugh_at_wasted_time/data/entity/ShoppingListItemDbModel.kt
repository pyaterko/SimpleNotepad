package com.owl_laugh_at_wasted_time.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list_table")
 data class ShoppingListItemDbModel (
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val text: String,
    var done: Boolean
)