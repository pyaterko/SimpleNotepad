package com.owl_laugh_at_wasted_time.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.owl_laugh_at_wasted_time.data.dao.*
import com.owl_laugh_at_wasted_time.data.entity.*

@Database(entities = [ItemCategoriesDbModel::class, ], version = 1, exportSchema = true)
abstract class CategoriesDataBase : RoomDatabase() {
    abstract fun categoriesDao():CategoriesDao

}