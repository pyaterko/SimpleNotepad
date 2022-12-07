package com.owl_laugh_at_wasted_time.data.model

import com.owl_laugh_at_wasted_time.data.dao.SubTaskDao
import com.owl_laugh_at_wasted_time.data.entity.SubItemDoneUpdate
import com.owl_laugh_at_wasted_time.data.entity.SubTaskItemDbModel
import com.owl_laugh_at_wasted_time.domain.entity.SubTaskItem
import com.owl_laugh_at_wasted_time.domain.repository.SubTaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class InSubTaskRepository @Inject constructor(
    private val subTaskDao: SubTaskDao
) : SubTaskRepository {

    override fun getAllDate(): Flow<List<SubTaskItem>> {
        val list = subTaskDao.getAllData()
        return list.map { listDbModel -> listDbModel.map { it.toSubTaskItem() } }
    }

    override suspend fun add(item: SubTaskItem) {
        val itemDbModel = SubTaskItemDbModel.toSubTaskItemDbModel(item)
        subTaskDao.add(itemDbModel)
    }

    override suspend fun delete(itemId: UUID) {
        subTaskDao.delete(itemId)
    }

    override fun getById(itemId: UUID): Flow<List<SubTaskItem>> {
        val list = subTaskDao.getItemsById(itemId)
        return list.map { listDbModel -> listDbModel.map { it.toSubTaskItem() } }
    }

    override suspend fun updateDone(id: String, done: Boolean) {
        subTaskDao.updateDone(SubItemDoneUpdate(id, done))
    }

    override suspend fun deleteItemById(itemId: String) {
        subTaskDao.deleteItemById(itemId)
    }
}


