package com.owl_laugh_at_wasted_time.data.model

import com.owl_laugh_at_wasted_time.data.dao.ItemToDoDao
import com.owl_laugh_at_wasted_time.data.mappers.ItemToDoMapper
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.domain.repository.ToDoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject


class InToDoRepository @Inject constructor(
    private val itemToDoDao: ItemToDoDao,
    private val mapper: ItemToDoMapper
) : ToDoRepository {

    override fun getAllDate()= runBlocking {
        val list = itemToDoDao.getAllData()
        list.map { mapper.mapListDbModelToListEntity(it) }
    }

    override suspend fun add(item: ItemToDo) {
        mapper.mapEntityToDbModel(item)?.let { itemToDoDao.add(it) }
    }

    override suspend fun delete(itemId: UUID) {
        itemToDoDao.delete(itemId)
    }

    override suspend fun getById(itemId: UUID): ItemToDo {
        val noteDBModel = itemToDoDao.getItemById(itemId)
        return mapper.mapDbModelToEntity(noteDBModel)
    }

}
