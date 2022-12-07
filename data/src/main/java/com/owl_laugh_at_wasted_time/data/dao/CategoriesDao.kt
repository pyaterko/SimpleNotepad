package com.owl_laugh_at_wasted_time.data.dao


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.owl_laugh_at_wasted_time.data.entity.ItemCategoriesDbModel


@Dao
interface CategoriesDao {

    @Query("SELECT * FROM category_table")
    fun getAllData(): LiveData<List<ItemCategoriesDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(noteDBModel: ItemCategoriesDbModel)

    @Query("DELETE FROM category_table WHERE id=:itemId")
    suspend fun delete(itemId: Int)


}