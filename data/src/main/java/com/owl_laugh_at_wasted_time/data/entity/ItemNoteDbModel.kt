package com.owl_laugh_at_wasted_time.data.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.owl_laugh_at_wasted_time.domain.entity.ItemColor
import java.text.SimpleDateFormat
import java.util.*
@Entity(tableName = "notes_table")
data class ItemNoteDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val text: String,
    val color: ItemColor,
    val dateOfCreation: String,

    )