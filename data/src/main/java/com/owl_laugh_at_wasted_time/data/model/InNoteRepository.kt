package com.owl_laugh_at_wasted_time.data.model

import com.owl_laugh_at_wasted_time.data.dao.ItemNoteDao
import com.owl_laugh_at_wasted_time.data.mappers.ItemNoteListMapper
import com.owl_laugh_at_wasted_time.domain.entity.ItemNote
import com.owl_laugh_at_wasted_time.domain.repository.NoteRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


class InNoteRepository @Inject constructor(
    private val itemNoteListDao: ItemNoteDao,
    private val mapper: ItemNoteListMapper
) : NoteRepository {

    override  suspend fun getLiveDate() = runBlocking {
        val x = itemNoteListDao.getAllData()
        x.map { list -> mapper.mapListDbModelToListEntity(list) }
    }

    override suspend fun add(item: ItemNote) {
        itemNoteListDao.addItemNote(mapper.mapEntityToDbModel(item))
    }

    override suspend fun delete(itemId: Int) {
        itemNoteListDao.deleteItemNote(itemId)
    }

    override suspend fun getById(itemId: Int): ItemNote {
        val noteDBModel = itemNoteListDao.getItemNoteById(itemId)
        return mapper.mapDbModelToEntity(noteDBModel)
    }

}