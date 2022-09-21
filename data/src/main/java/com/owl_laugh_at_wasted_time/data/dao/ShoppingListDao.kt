package com.owl_laugh_at_wasted_time.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.owl_laugh_at_wasted_time.data.entity.ShoppingListItemDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {
    @Query("SELECT * FROM shopping_list_table")
     fun getAllData(): Flow<List<ShoppingListItemDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addShoppingListItem(shoppingListItem: ShoppingListItemDbModel)

    @Query("DELETE FROM shopping_list_table WHERE id=:shoppingListItemId")
    suspend fun deleteShoppingListItem(shoppingListItemId: Int)
}