package com.owl_laugh_at_wasted_time.domain.repository

import androidx.lifecycle.LiveData
import com.owl_laugh_at_wasted_time.domain.entity.ShoppingListItem
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {
     fun getAllData(): Flow<List<ShoppingListItem>>
    suspend fun add(shoppingListItem: ShoppingListItem)
    suspend fun delete(shoppingListItemId: Int)
}
