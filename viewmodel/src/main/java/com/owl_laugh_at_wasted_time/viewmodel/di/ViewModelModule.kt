package com.owl_laugh_at_wasted_time.viewmodel.di

import androidx.lifecycle.ViewModel
import com.owl_laugh_at_wasted_time.viewmodel.notes.NotesListViewModel
import com.owl_laugh_at_wasted_time.viewmodel.shopping.ShoppingListViewModel
import com.owl_laugh_at_wasted_time.viewmodel.todo.TodoListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ShoppingListViewModel::class)
    fun bindShoppingListViewModel(viewModel: ShoppingListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotesListViewModel::class)
    fun bindNotesListViewModel(viewModel: NotesListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TodoListViewModel::class)
    fun bindTodoListViewModel(viewModel: TodoListViewModel): ViewModel
}