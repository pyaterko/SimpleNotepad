package com.owl_laugh_at_wasted_time.viewmodel.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.domain.entity.SubTaskItem
import com.owl_laugh_at_wasted_time.domain.repository.SubTaskRepository
import com.owl_laugh_at_wasted_time.domain.repository.ToDoRepository
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


class TodoListViewModel @Inject constructor(
    private val toDoRepository: ToDoRepository,
    private val subTaskRepository: SubTaskRepository
) : ViewModel() {

    val liveData = toDoRepository.getAllDate()

    fun flowSubTask(itemId: UUID) = subTaskRepository.getById(itemId)


    fun addSubTask(item: SubTaskItem) {
        viewModelScope.launch {
            subTaskRepository.add(item)
        }
    }

    fun deleteSubTask(itemId: UUID) {
        viewModelScope.launch {
            subTaskRepository.delete(itemId)
        }
    }

    fun updateDoneSubTask(id: String, done: Boolean) {
        viewModelScope.launch {
            subTaskRepository.updateDone(id, done)
        }
    }

    fun deleteItemById(itemId: String) {
        viewModelScope.launch {
            subTaskRepository.deleteItemById(itemId)
        }
    }

    suspend fun getNoteById(noteId: UUID) =
        toDoRepository.getById(noteId)

    fun addToDo(item: ItemToDo) {
        viewModelScope.launch {
            toDoRepository.add(item)
        }
    }

    fun deleteItem(itemId: UUID) {
        viewModelScope.launch {
            toDoRepository.delete(itemId)
        }

    }

}