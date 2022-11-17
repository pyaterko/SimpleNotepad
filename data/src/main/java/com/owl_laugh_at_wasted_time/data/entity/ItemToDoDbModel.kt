package com.owl_laugh_at_wasted_time.data.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.owl_laugh_at_wasted_time.domain.entity.ItemColor
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import java.util.*

@Entity(tableName = "todo_table")
data class ItemToDoDbModel(
    @PrimaryKey
    val id: UUID,
    val title: String,
    val color: ItemColor,
    val dateOfCreation: String,
    var data: String,
    var done: Boolean
){
    fun toItemToDo() = ItemToDo(
        id = id,
        title = title,
        done =  done  ,
        color = color,
        dateOfCreation = dateOfCreation,
        data =data
    )
    companion object{
        fun fromItemToDo(item: ItemToDo) = item.id?.let {
            ItemToDoDbModel(
                id = it,
                title = item.title,
                done = item. done  ,
                color = item.color,
                dateOfCreation = item.dateOfCreation,
                data = item.data
            )
        }
    }
}