package com.owl_laugh_at_wasted_time.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.owl_laugh_at_wasted_time.data.dao.ItemNoteDao
import com.owl_laugh_at_wasted_time.data.dao.ItemToDoDao
import com.owl_laugh_at_wasted_time.data.dao.ShoppingListDao
import com.owl_laugh_at_wasted_time.data.dao.SubTaskDao
import com.owl_laugh_at_wasted_time.data.entity.ItemNoteDbModel
import com.owl_laugh_at_wasted_time.data.entity.ItemToDoDbModel
import com.owl_laugh_at_wasted_time.data.entity.ShoppingListItemDbModel
import com.owl_laugh_at_wasted_time.data.entity.SubTaskItemDbModel

@Database(
    entities =
    [
        ItemNoteDbModel::class,
        ShoppingListItemDbModel::class,
        ItemToDoDbModel::class,
        SubTaskItemDbModel::class
    ],
    version = 2,
    exportSchema = false
)
abstract class NotePadDataBase : RoomDatabase() {
    abstract fun itemNoteDao(): ItemNoteDao
    abstract fun dictionaryDao(): ItemToDoDao
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun subTaskDao():SubTaskDao

}