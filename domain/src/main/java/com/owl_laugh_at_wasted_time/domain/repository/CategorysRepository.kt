package com.owl_laugh_at_wasted_time.domain.repository

import androidx.lifecycle.LiveData
import com.owl_laugh_at_wasted_time.domain.entity.ItemCategory
import com.owl_laugh_at_wasted_time.domain.entity.ItemNote
import kotlinx.coroutines.flow.Flow

interface CategorysRepository {
    fun getAllData():LiveData<List<ItemCategory>>
    suspend fun updateCategory(categoryItem: ItemCategory,list: List<ItemCategory>): List<ItemCategory>
    suspend fun populateInitialData()
    suspend fun add(itemDBModel: ItemCategory)
    suspend fun delete(itemId: Int)
}