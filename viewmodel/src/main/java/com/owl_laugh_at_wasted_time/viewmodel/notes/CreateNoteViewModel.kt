package com.owl_laugh_at_wasted_time.viewmodel.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owl_laugh_at_wasted_time.domain.UNDEFINED_ID
import com.owl_laugh_at_wasted_time.domain.entity.ItemCategory
import com.owl_laugh_at_wasted_time.domain.entity.ItemNote
import com.owl_laugh_at_wasted_time.domain.repository.CategorysRepository
import com.owl_laugh_at_wasted_time.domain.repository.NoteRepository
import com.owl_laugh_at_wasted_time.domain.repository.UiActions
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateNoteViewModel @Inject constructor(
    private val repositoryNote: NoteRepository,
    private val categorysRepository: CategorysRepository
) : ViewModel() {

    val categoriesLiveData = categorysRepository.getAllData()

    suspend fun getNoteById(noteId: Int) =
        repositoryNote.getById(noteId)


    suspend fun addItemNote(item: ItemNote) {
        repositoryNote.add(item)
    }

    fun addCategory(name:String) {
       viewModelScope.launch {
           categorysRepository.add(ItemCategory(UNDEFINED_ID,name))
       }
    }
    fun deleteCategory(id:Int) {
        viewModelScope.launch {
            categorysRepository.delete(id)
        }
    }
}

