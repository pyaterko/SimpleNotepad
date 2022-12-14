package com.owl_laugh_at_wasted_time.domain.repository

import androidx.lifecycle.LiveData
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import java.util.*

interface ToDoRepository {
    fun getAllDate(): LiveData<List<ItemToDo>>
    suspend fun add(item: ItemToDo)
    suspend fun delete(itemId: UUID)
    suspend fun getById(itemId: UUID): ItemToDo

}