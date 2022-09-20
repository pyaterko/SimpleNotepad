package com.owl_laugh_at_wasted_time.data.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.owl_laugh_at_wasted_time.domain.entity.ItemColor
import com.owl_laugh_at_wasted_time.domain.entity.PriorityToDo
import java.text.SimpleDateFormat
import java.util.*
@Entity(tableName = "todo_table")
data class ItemToDoDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val text: String,
    val color: ItemColor,
    val dateOfCreation: String,
    var priority: PriorityToDo,
    var data: String
)