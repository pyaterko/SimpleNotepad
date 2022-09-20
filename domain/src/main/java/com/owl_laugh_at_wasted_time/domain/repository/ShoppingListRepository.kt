package com.owl_laugh_at_wasted_time.domain.repository

import androidx.lifecycle.LiveData
import com.owl_laugh_at_wasted_time.domain.entity.ShoppingListItem
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {
    suspend fun getAllData(): Flow<List<ShoppingListItem>>
    suspend fun addShoppingListItem(shoppingListItem: ShoppingListItem)
    suspend fun deleteShoppingListItem(shoppingListItemId: Int)
}
