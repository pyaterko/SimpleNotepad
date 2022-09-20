package com.owl_laugh_at_wasted_time.data.dao


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.owl_laugh_at_wasted_time.data.entity.ItemToDoDbModel
import kotlinx.coroutines.flow.Flow


@Dao
interface ItemToDoDao {

    @Query("SELECT * FROM todo_table")
    suspend fun getAllData(): Flow<List<ItemToDoDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItemNote(noteDBModel: ItemToDoDbModel)

    @Query("DELETE FROM todo_table WHERE id=:itemId")
    suspend fun deleteItemNote(itemId: Int)

    @Query("SELECT * FROM todo_table WHERE id=:itemId LIMIT 1")
    suspend fun getItemNoteById(itemId: Int): ItemToDoDbModel

}