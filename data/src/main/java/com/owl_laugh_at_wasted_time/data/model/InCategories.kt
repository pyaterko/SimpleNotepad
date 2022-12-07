package com.owl_laugh_at_wasted_time.data.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.owl_laugh_at_wasted_time.data.dao.CategoriesDao
import com.owl_laugh_at_wasted_time.data.entity.ItemCategoriesDbModel
import com.owl_laugh_at_wasted_time.domain.entity.ItemCategory
import com.owl_laugh_at_wasted_time.domain.repository.CategorysRepository
import com.owl_laugh_at_wasted_time.domain.repository.UiActions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InCategories @Inject constructor(
    private val categoriesDao: CategoriesDao
) : CategorysRepository {


    override suspend fun updateCategory(categoryItem: ItemCategory,list: List<ItemCategory>): List<ItemCategory> {
        val categoryItems = arrayListOf<ItemCategory>()
        val index = categoryItem.id
        if (index != -1) {
            categoryItems[index] = categoryItem.copy(state = true)
        }
        return categoryItems
    }

    override fun getAllData(): LiveData<List<ItemCategory>> = Transformations.map(
   categoriesDao.getAllData()
    ){
        it.map { itenDbModel -> itenDbModel.toItemCategory() }
    }

    override suspend fun populateInitialData() {
        val list = listOf(
            ItemCategoriesDbModel(1, "Все"),
            ItemCategoriesDbModel(2, "Мысли"),
            ItemCategoriesDbModel(3, "Личное"),
            ItemCategoriesDbModel(4, "Поздравления"),
        )
        for (cat in list) {
            categoriesDao.add(cat)
        }
    }

    override suspend fun add(itemDBModel: ItemCategory) {
      categoriesDao.add(ItemCategoriesDbModel.toItemCategoriesDbModel(itemDBModel))
    }

    override suspend fun delete(itemId: Int) {
       categoriesDao.delete(itemId)
    }
}