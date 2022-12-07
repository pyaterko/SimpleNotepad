package com.owl_laugh_at_wasted_time.domain.repository

import com.owl_laugh_at_wasted_time.domain.entity.ItemNote
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getLiveDate(): Flow<List<ItemNote>>
    suspend fun add(item: ItemNote)
    suspend fun delete(itemId: Int)
    suspend fun getById(itemId: Int): ItemNote
    suspend fun getItemsByCategory(category: String): List<ItemNote>

}