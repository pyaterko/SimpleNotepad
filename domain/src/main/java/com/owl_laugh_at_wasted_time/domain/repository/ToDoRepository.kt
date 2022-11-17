package com.owl_laugh_at_wasted_time.domain.repository

import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ToDoRepository {
    fun getAllDate(): Flow<List<ItemToDo>>
    suspend fun add(item: ItemToDo)
    suspend fun delete(itemId: UUID)
    suspend fun getById(itemId: UUID): ItemToDo

}