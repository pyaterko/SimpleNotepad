package com.owl_laugh_at_wasted_time.domain.repository

import androidx.lifecycle.LiveData
import com.owl_laugh_at_wasted_time.domain.entity.ItemNote
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import kotlinx.coroutines.flow.Flow

interface ToDoRepository {
    suspend fun getLiveDate(): Flow<List<ItemToDo>>
    suspend fun add(item: ItemToDo)
    suspend fun delete(itemId: Int)
    suspend fun getById(itemId: Int): ItemToDo

}