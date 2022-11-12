package com.owl_laugh_at_wasted_time.viewmodel.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.domain.repository.ToDoRepository
import kotlinx.coroutines.launch
import javax.inject.Inject


class TodoListViewModel @Inject constructor(
    private val repository: ToDoRepository,
) : ViewModel() {

    val listNotes = repository.getAllDate()

    suspend fun getNoteById(noteId: Int) =
        repository.getById(noteId)

     fun addItemNote(item: ItemToDo) {
         viewModelScope.launch {
             repository.add(item)
         }

    }

     fun deleteNote(itemNote: ItemToDo) {
        viewModelScope.launch {
            repository.delete(itemNote.id)
        }

    }
}