package com.owl_laugh_at_wasted_time.data.dao

import androidx.room.*
import com.owl_laugh_at_wasted_time.data.entity.SubItemDoneUpdate
import com.owl_laugh_at_wasted_time.data.entity.SubTaskItemDbModel
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface SubTaskDao {

    @Query("SELECT * FROM subtask_table")
    fun getAllData(): Flow<List<SubTaskItemDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(itemToDoDbModel: SubTaskItemDbModel)

    @Update(entity = SubTaskItemDbModel::class)
    suspend fun updateDone(updateDone: SubItemDoneUpdate)

    @Query("DELETE FROM subtask_table WHERE idParent=:itemId")
    suspend fun delete(itemId: UUID)

    @Query("SELECT * FROM subtask_table WHERE idParent=:itemId")
    fun getItemsById(itemId: UUID): Flow<List<SubTaskItemDbModel>>

    @Query("DELETE FROM subtask_table WHERE id=:itemId")
    suspend fun deleteItemById(itemId: String)
}