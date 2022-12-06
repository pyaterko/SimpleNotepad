package com.owl_laugh_at_wasted_time.viewmodel.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owl_laugh_at_wasted_time.domain.entity.ShoppingListItem
import com.owl_laugh_at_wasted_time.domain.repository.ShoppingListRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class ShoppingListViewModel @Inject constructor(
    private val repository: ShoppingListRepository
) : ViewModel() {

    val shoppingList = repository.getAllData()

    fun deleteShoppingListItem(shoppingListItem: ShoppingListItem) {
        viewModelScope.launch {
            repository.delete(shoppingListItem.id)
        }
    }

    fun addShoppingListItem(shoppingListItem: ShoppingListItem) {
        viewModelScope.launch {
            repository.add(shoppingListItem)
        }
    }


}