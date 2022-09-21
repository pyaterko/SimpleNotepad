package com.owl_laugh_at_wasted_time.viewmodel.shopping

import androidx.lifecycle.ViewModel
import com.owl_laugh_at_wasted_time.domain.entity.ShoppingListItem
import com.owl_laugh_at_wasted_time.domain.repository.ShoppingListRepository
import javax.inject.Inject

class ShoppingListViewModel @Inject constructor(
    private val repository: ShoppingListRepository
) : ViewModel() {

     val shoppingList = repository.getAllData()

    suspend fun deleteShoppingListItem(shoppingListItem: ShoppingListItem) {
        repository.deleteShoppingListItem(shoppingListItem.id)
    }

    suspend fun addShoppingListItem(shoppingListItem: ShoppingListItem) {
        repository.addShoppingListItem(shoppingListItem)
    }
}