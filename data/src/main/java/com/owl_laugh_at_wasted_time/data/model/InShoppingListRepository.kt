package com.owl_laugh_at_wasted_time.data.model

import com.owl_laugh_at_wasted_time.data.dao.ShoppingListDao
import com.owl_laugh_at_wasted_time.data.mappers.ShoppingListMapper
import com.owl_laugh_at_wasted_time.domain.entity.ShoppingListItem
import com.owl_laugh_at_wasted_time.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class InShoppingListRepository @Inject constructor(
    private val shoppingListDao: ShoppingListDao,
    private val mapper: ShoppingListMapper
) : ShoppingListRepository {

    override suspend fun getAllData() = runBlocking {
        val x = shoppingListDao.getAllData()
        x.map { list -> mapper.mapListDbModelToListEntity(list) }
    }


    override suspend fun addShoppingListItem(shoppingListItem: ShoppingListItem) {
        shoppingListDao.addShoppingListItem(mapper.mapEntityToDbModel(shoppingListItem))
    }

    override suspend fun deleteShoppingListItem(shoppingListItemId: Int) {
        shoppingListDao.deleteShoppingListItem(shoppingListItemId)
    }
}