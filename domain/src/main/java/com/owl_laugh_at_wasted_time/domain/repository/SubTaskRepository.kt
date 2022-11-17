package com.owl_laugh_at_wasted_time.domain.repository

import com.owl_laugh_at_wasted_time.domain.entity.SubTaskItem
import kotlinx.coroutines.flow.Flow
import java.util.*

interface SubTaskRepository {
    fun getAllDate(): Flow<List<SubTaskItem>>
    suspend fun add(item: SubTaskItem)
    suspend fun delete(itemId: UUID)
    fun getById(itemId: UUID): Flow<List<SubTaskItem>>
    suspend fun updateDone(id: String, done: Boolean)
    suspend fun deleteItemById(itemId: String)
}
