package com.owl_laugh_at_wasted_time.viewmodel.todo

import androidx.lifecycle.viewModelScope
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.domain.repository.ToDoRepository
import com.owl_laugh_at_wasted_time.viewmodel.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


class TodoListViewModel @Inject constructor(
    private val repository: ToDoRepository,
) : BaseViewModel() {

    val flow = repository.getAllDate()

    suspend fun getNoteById(noteId: Int) =
        repository.getById(noteId)

    fun addItemNote(item: ItemToDo) {
        viewModelScopeCoroutine.launch {
            repository.add(item)
        }
    }

    fun deleteNote(itemId: Int) {
        viewModelScope.launch {
            repository.delete(itemId)
        }

    }

    override fun handleError(throwable: Throwable) {}
}