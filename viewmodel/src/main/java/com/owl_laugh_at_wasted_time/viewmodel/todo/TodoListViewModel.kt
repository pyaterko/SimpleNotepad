package com.owl_laugh_at_wasted_time.viewmodel.todo

import androidx.lifecycle.viewModelScope
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.domain.repository.ToDoRepository
import com.owl_laugh_at_wasted_time.viewmodel.base.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


class TodoListViewModel @Inject constructor(
    private val repository: ToDoRepository,
) : BaseViewModel() {

    val flow = repository.getAllDate()

    suspend fun getNoteById(noteId: UUID) =
        repository.getById(noteId)

    fun addToDo(item: ItemToDo) {
        viewModelScopeCoroutine.launch {
            repository.add(item)
        }
    }

    fun deleteNote(itemId: UUID) {
        viewModelScope.launch {
            repository.delete(itemId)
        }

    }

    override fun handleError(throwable: Throwable) {}
}