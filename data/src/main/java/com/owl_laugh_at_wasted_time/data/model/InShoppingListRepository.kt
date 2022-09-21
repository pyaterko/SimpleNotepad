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

    override fun getAllData()= runBlocking {
        val list = shoppingListDao.getAllData()
        list.map { mapper.mapListDbModelToListEntity(it) }
    }

    override suspend fun add(shoppingListItem: ShoppingListItem) {
        shoppingListDao.add(mapper.mapEntityToDbModel(shoppingListItem))
    }

    override suspend fun delete(shoppingListItemId: Int) {
        shoppingListDao.delete(shoppingListItemId)
    }
}