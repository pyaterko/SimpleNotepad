package com.owl_laugh_at_wasted_time.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.owl_laugh_at_wasted_time.data.entity.ItemToDoDbModel
import kotlinx.coroutines.flow.Flow
import java.util.*


@Dao
interface ItemToDoDao {

    @Query("SELECT * FROM todo_table")
    fun getAllData(): Flow<List<ItemToDoDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(noteDBModel: ItemToDoDbModel)

    @Query("DELETE FROM todo_table WHERE id=:itemId")
    suspend fun delete(itemId: UUID)

    @Query("SELECT * FROM todo_table WHERE id=:itemId LIMIT 1")
    suspend fun getItemById(itemId: UUID): ItemToDoDbModel

}