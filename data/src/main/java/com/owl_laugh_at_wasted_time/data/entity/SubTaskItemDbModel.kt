package com.owl_laugh_at_wasted_time.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "subtask_table")
 data class SubTaskItemDbModel (
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val idParent:UUID,
    val text: String,
    var done: Boolean
)