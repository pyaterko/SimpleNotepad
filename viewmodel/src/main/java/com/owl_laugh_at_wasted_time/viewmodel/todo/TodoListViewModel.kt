package com.owl_laugh_at_wasted_time.viewmodel.todo

import androidx.lifecycle.viewModelScope
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.domain.entity.SubTaskItem
import com.owl_laugh_at_wasted_time.domain.repository.SubTaskRepository
import com.owl_laugh_at_wasted_time.domain.repository.ToDoRepository
import com.owl_laugh_at_wasted_time.viewmodel.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


class TodoListViewModel @Inject constructor(
    private val toDoRepository: ToDoRepository,
    private val subTaskRepository: SubTaskRepository
) : BaseViewModel() {

    val flowToDo = toDoRepository.getAllDate()

    fun flowSubTask(itemId: UUID)=subTaskRepository.getById(itemId)

     fun addSubTask(item: SubTaskItem){
        viewModelScopeCoroutine.launch {
            subTaskRepository.add(item)
        }
    }

     fun deleteSubTask(itemId: UUID){
         viewModelScopeCoroutine.launch {
             subTaskRepository.delete(itemId)
         }
     }

     fun updateDoneSubTask(  id: String, done: Boolean){
         viewModelScopeCoroutine.launch {
             subTaskRepository.updateDone(id, done)
         }
     }

     fun deleteItemById(itemId: String){
        viewModelScopeCoroutine.launch{
            subTaskRepository.deleteItemById(itemId)
        }
    }

    suspend fun getNoteById(noteId: UUID) =
        toDoRepository.getById(noteId)

    fun addToDo(item: ItemToDo) {
        viewModelScopeCoroutine.launch {
            toDoRepository.add(item)
        }
    }

    fun deleteItem(itemId: UUID) {
        viewModelScope.launch {
            toDoRepository.delete(itemId)
        }

    }

    override fun handleError(throwable: Throwable) {}
}