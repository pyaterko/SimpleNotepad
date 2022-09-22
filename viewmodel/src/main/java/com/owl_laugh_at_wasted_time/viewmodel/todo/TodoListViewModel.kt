package com.owl_laugh_at_wasted_time.viewmodel.todo

import androidx.lifecycle.ViewModel
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.domain.repository.ToDoRepository
import javax.inject.Inject


class TodoListViewModel @Inject constructor(
    private val repository: ToDoRepository,
) : ViewModel() {

    val listNotes = repository.getAllDate()

    suspend fun getNoteById(noteId: Int) =
        repository.getById(noteId)

    suspend fun addItemNote(item: ItemToDo) {
        repository.add(item)
    }

    suspend fun deleteNote(itemNote: ItemToDo) {
        repository.delete(itemNote.id)
    }
}