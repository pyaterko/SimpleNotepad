package com.owl_laugh_at_wasted_time.data.di

import android.content.Context
import androidx.room.Room
import com.owl_laugh_at_wasted_time.data.NotePadDataBase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface DataModule {

    companion object {

        @Singleton
        @Provides
        fun provideDataBase(context: Context) =
            Room.databaseBuilder(context, NotePadDataBase::class.java, "notepad_data_base")
                .fallbackToDestructiveMigration()
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

    }
}