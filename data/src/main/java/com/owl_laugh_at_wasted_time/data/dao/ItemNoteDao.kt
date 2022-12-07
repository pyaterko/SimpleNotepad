package com.owl_laugh_at_wasted_time.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.owl_laugh_at_wasted_time.data.entity.ItemNoteDbModel
import kotlinx.coroutines.flow.Flow


@Dao
interface ItemNoteDao {

    @Query("SELECT * FROM notes_table")
    fun getAllData(): Flow<List<ItemNoteDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(noteDBModel: ItemNoteDbModel)

    @Query("DELETE FROM notes_table WHERE id=:itemId")
    suspend fun delete(itemId: Int)

    @Query("SELECT * FROM notes_table WHERE id=:itemId LIMIT 1")
    suspend fun getItemById(itemId: Int): ItemNoteDbModel

    @Query("SELECT * FROM notes_table WHERE category=:category")
    suspend fun getItemsByCategory(category: String): List<ItemNoteDbModel>

}