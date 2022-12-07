package com.owl_laugh_at_wasted_time.simplenotepad.di

import com.owl_laugh_at_wasted_time.data.model.*
import com.owl_laugh_at_wasted_time.domain.repository.*
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.actions.AndroidUiActions
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface RepositoryModule {


    @Singleton
    @Binds
    fun bindShoppingListRepository(impl: InShoppingListRepository): ShoppingListRepository

    @Singleton
    @Binds
    fun bindSubTaskRepository(impl: InSubTaskRepository): SubTaskRepository

    @Singleton
    @Binds
    fun bindNoteRepository(impl: InNoteRepository): NoteRepository


    @Singleton
    @Binds
    fun bindToDoRepository(impl: InToDoRepository): ToDoRepository

    @Singleton
    @Binds
    fun bindCategoriesRepository(impl: InCategories): CategoriesRepository

    @Singleton
    @Binds
    fun bindUiActions(impl: AndroidUiActions): UiActions
}
