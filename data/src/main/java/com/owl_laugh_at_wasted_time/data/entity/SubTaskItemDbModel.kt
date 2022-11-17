package com.owl_laugh_at_wasted_time.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.owl_laugh_at_wasted_time.domain.entity.SubTaskItem
import java.util.*

@Entity(tableName = "subtask_table")
data class SubTaskItemDbModel(
    @PrimaryKey()
    val id: String,
    val idParent: UUID,
    var done: Boolean
) {
    fun toSubTaskItem() = SubTaskItem(
        text = id,
        done = done,
        idParent = idParent
    )

    companion object {
        fun toSubTaskItemDbModel(item: SubTaskItem) = SubTaskItemDbModel(
            id = item.text,
            done = item.done,
            idParent = item.idParent
        )
    }

}