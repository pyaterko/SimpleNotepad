package com.owl_laugh_at_wasted_time.simplenotepad.di

import com.owl_laugh_at_wasted_time.data.model.InNoteRepository
import com.owl_laugh_at_wasted_time.data.model.InShoppingListRepository
import com.owl_laugh_at_wasted_time.data.model.InToDoRepository
import com.owl_laugh_at_wasted_time.domain.repository.NoteRepository
import com.owl_laugh_at_wasted_time.domain.repository.ShoppingListRepository
import com.owl_laugh_at_wasted_time.domain.repository.ToDoRepository
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
    fun bindNoteRepository(impl: InNoteRepository): NoteRepository


    @Singleton
    @Binds
    fun bindToDoRepository(impl: InToDoRepository): ToDoRepository
}