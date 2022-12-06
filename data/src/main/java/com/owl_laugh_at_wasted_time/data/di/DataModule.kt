package com.owl_laugh_at_wasted_time.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.owl_laugh_at_wasted_time.data.CategoriesDataBase
import com.owl_laugh_at_wasted_time.data.NotePadDataBase
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
interface DataModule {

    companion object {

        @Singleton
        @Provides
        fun provideDataBaseCategories(context: Context) =
            Room.databaseBuilder(context, CategoriesDataBase::class.java, "note_categories_data_base")
                .fallbackToDestructiveMigration()
                .build().categoriesDao()

        @Singleton
        @Provides
        fun provideDataBase(context: Context) =
            Room.databaseBuilder(context, NotePadDataBase::class.java, "notepad_data_base")
               .addMigrations(migration_1_2)
                .build()

        @Singleton
        @Provides
        fun provideShopListDao(
            context: Context
        ) =
            provideDataBase(context).shoppingListDao()

        @Singleton
        @Provides
        fun provideDictionaryDao(
            context: Context
        ) =
            provideDataBase(context).dictionaryDao()

        @Singleton
        @Provides
        fun provideItemNoteDao(
            context: Context
        ) =
            provideDataBase(context).itemNoteDao()

        @Singleton
        @Provides
        fun provideSubTaskDao(
            context: Context
        ) =
            provideDataBase(context).subTaskDao()
    }
}
private val migration_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE notes_table ADD COLUMN category TEXT NOT NULL DEFAULT ''"
        )
    }
}