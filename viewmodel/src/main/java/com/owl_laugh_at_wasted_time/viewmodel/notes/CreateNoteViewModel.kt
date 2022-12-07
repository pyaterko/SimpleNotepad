package com.owl_laugh_at_wasted_time.viewmodel.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owl_laugh_at_wasted_time.domain.UNDEFINED_ID
import com.owl_laugh_at_wasted_time.domain.entity.ItemCategory
import com.owl_laugh_at_wasted_time.domain.entity.ItemNote
import com.owl_laugh_at_wasted_time.domain.repository.CategoriesRepository
import com.owl_laugh_at_wasted_time.domain.repository.NoteRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateNoteViewModel @Inject constructor(
    private val repositoryNote: NoteRepository,
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {

    val categoriesLiveData = categoriesRepository.getAllData()

    suspend fun getNoteById(noteId: Int) =
        repositoryNote.getById(noteId)


    suspend fun addItemNote(item: ItemNote) {
        repositoryNote.add(item)
    }

    fun addCategory(name:String) {
       viewModelScope.launch {
           categoriesRepository.add(ItemCategory(UNDEFINED_ID,name))
       }
    }
    fun deleteCategory(id:Int) {
        viewModelScope.launch {
            categoriesRepository.delete(id)
        }
    }
}

