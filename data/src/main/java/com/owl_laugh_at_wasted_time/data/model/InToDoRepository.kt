package com.owl_laugh_at_wasted_time.data.model

import com.owl_laugh_at_wasted_time.data.dao.ItemToDoDao
import com.owl_laugh_at_wasted_time.data.mappers.ItemToDoMapper
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.domain.repository.ToDoRepository
import javax.inject.Inject


class InToDoRepository @Inject constructor(
    private val itemToDoDao: ItemToDoDao,
    private val mapper: ItemToDoMapper
) : ToDoRepository {

    override suspend fun getLiveDate() =
        mapper.mapListDbModelToListEntity(itemToDoDao.getAllData())


    override suspend fun add(item: ItemToDo) {
        itemToDoDao.addItemNote(mapper.mapEntityToDbModel(item))
    }

    override suspend fun delete(itemId: Int) {
        itemToDoDao.deleteItemNote(itemId)
    }

    override suspend fun getById(itemId: Int): ItemToDo {
        val noteDBModel = itemToDoDao.getItemNoteById(itemId)
        return mapper.mapDbModelToEntity(noteDBModel)
    }

}
