package com.owl_laugh_at_wasted_time.data.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.owl_laugh_at_wasted_time.data.dao.ItemToDoDao
import com.owl_laugh_at_wasted_time.data.entity.ItemToDoDbModel
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.domain.repository.ToDoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject


class InToDoRepository @Inject constructor(
    private val itemToDoDao: ItemToDoDao
) : ToDoRepository {

    override fun getAllDate(): LiveData<List<ItemToDo>> =Transformations.map(
        itemToDoDao.getAllData()
    ){
     it.map { itenDbModel -> itenDbModel.toItemToDo() }
    }

    override suspend fun add(item: ItemToDo) {
        val itemDbModel = ItemToDoDbModel.fromItemToDo(item)
        itemDbModel?.let { itemToDoDao.add(it) }
    }

    override suspend fun delete(itemId: UUID) {
        itemToDoDao.delete(itemId)
    }

    override suspend fun getById(itemId: UUID): ItemToDo {
        val noteDBModel = itemToDoDao.getItemById(itemId)
        return noteDBModel.toItemToDo()
    }

}
