package com.owl_laugh_at_wasted_time.viewmodel.notes

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owl_laugh_at_wasted_time.domain.entity.ItemColor
import com.owl_laugh_at_wasted_time.domain.entity.ItemNote
import com.owl_laugh_at_wasted_time.domain.entity.NotesListItem
import com.owl_laugh_at_wasted_time.domain.repository.*
import com.owl_laugh_at_wasted_time.viewmodel.R
import com.owl_laugh_at_wasted_time.viewmodel.notes.multichoice.SelectAllOperation
import com.owl_laugh_at_wasted_time.viewmodel.notes.multichoice.State
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.regex.Pattern
import javax.inject.Inject


class NotesListViewModel @Inject constructor(
    uiActions: UiActions,
    private val repositoryNote: NoteRepository,
    private val multiChoiceHandler: MultiChoiceHandler<ItemNote>,
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {

    val listNotes = repositoryNote.getLiveDate()
    val categoriesLiveData = categoriesRepository.getAllData()

    private val _stateLiveData = MutableLiveData<State<NotesListItem>>()
    val stateLiveData: LiveData<State<NotesListItem>> = _stateLiveData


    init {
        if (uiActions.isPopulateInitialData()) {
            viewModelScope.launch {
                delay(1000)
                categoriesRepository.populateInitialData()
            }
            uiActions.notPopulateInitialData()
        }

        viewModelScope.launch {
            multiChoiceHandler.setItemsFlow(viewModelScope, repositoryNote.getLiveDate())
            val combinedFlow = combine(
                repositoryNote.getLiveDate(),
                multiChoiceHandler.listen(),
                ::merge
            )
            combinedFlow.collectLatest {
                _stateLiveData.value = it
            }
        }
    }

    fun selectOrClearAll() {
        stateLiveData.value?.selectAllOperation?.operation?.invoke()
    }

    fun toggleSelection(item: NotesListItem) {
        multiChoiceHandler.toggle(item.originItem)

    }

    fun deleteSelectedItems() {
        viewModelScope.launch {
            val currentMultiChoiceState = multiChoiceHandler.listen().first()
            repositoryNote.deleteSelected(currentMultiChoiceState)
        }
    }

    suspend fun listCategories(category: String) =
        viewModelScope.async {
            return@async repositoryNote.getItemsByCategory(category)
        }.await()

    private suspend fun addItemNote(item: ItemNote) {
        repositoryNote.add(item)
    }

    suspend fun deleteNote(itemId: Int) {
        repositoryNote.delete(itemId)
    }

    fun save(context: Context, fileName: String, text: String) {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    saveFile(context, fileName, text)
                }
            }.onSuccess {
                Toast.makeText(
                    context,
                    "${context.getString(R.string.success_save)} $fileName",
                    Toast.LENGTH_SHORT
                ).show()
            }.onFailure {
                Toast.makeText(context, "некорректное имя файла", Toast.LENGTH_LONG).show()
            }

        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun saveFile(context: Context, fileName: String, text: String) {
        val outputStream: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
            val extVolumeUri: Uri = MediaStore.Files.getContentUri("external")
            val fileUri: Uri? = context.contentResolver.insert(extVolumeUri, values)
            context.contentResolver.openOutputStream(fileUri!!)
        } else {
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString()
            val file = File(path, "$fileName.txt")
            FileOutputStream(file)
        }
        val bytes = text.toByteArray()
        outputStream?.write(bytes)
        outputStream?.close()
    }

    fun restoreNote(line: String, name: String): String {
        val dateOfCreationPattern = Pattern.compile("<<dateOfCreation:(.*?)>>")
        val colorPattern = Pattern.compile("<<color:(.*?)>>")
        val titlePattern = Pattern.compile("<<title:(.*?)>>")

        val dateOfCreationMatcher = dateOfCreationPattern.matcher(line)
        val colorMatcher = colorPattern.matcher(line)
        val titleMatcher = titlePattern.matcher(line)

        val id = 0
        var dateOfCreation = ""
        var color = ""
        var title = ""
        val listStr = line.split('\n')
        val preliminaryText = listStr.subList(4, (listStr.size))
        val sb = StringBuilder()
        preliminaryText.forEach { sb.append(it).append('\n') }

        while (dateOfCreationMatcher.find()) {
            dateOfCreation = dateOfCreationMatcher.group(1) as String
        }
        while (colorMatcher.find()) {
            color = colorMatcher.group(1) as String
        }
        while (titleMatcher.find()) {
            title = titleMatcher.group(1) as String
        }

        val text = sb.toString()
        viewModelScope.launch {
            if (dateOfCreation.isNotBlank()) {
                addItemNote(
                    ItemNote(
                        id,
                        title.trim(),
                        text.trim(),
                        color.getItemNoteColor(),
                        dateOfCreation
                    )
                )
            } else {
                addItemNote(
                    ItemNote(
                        id,
                        name.trim(),
                        line.trim(),
                        ItemColor.WHITE
                    )
                )
            }
        }
        return title
    }

    private fun merge(
        listItemNote: List<ItemNote>,
        multiChoiceState: MultiChoiceState<ItemNote>
    ): State<NotesListItem> {
        return State(
            list = listItemNote.map { item ->
                NotesListItem(item, multiChoiceState.isChecked(item))
            },
            totalCount = listItemNote.size,
            totalCheckedCount = multiChoiceState.totalCheckedCount,
            selectAllOperation = if (multiChoiceState.totalCheckedCount < listItemNote.size) {
                SelectAllOperation(R.string.select_all, multiChoiceHandler::selectAll)
            } else {
                SelectAllOperation(R.string.clear_all, multiChoiceHandler::clearAll)
            }
        )
    }

}

fun String.getItemNoteColor() = when (this) {
    "WHITE" -> ItemColor.WHITE
    "VIOLET" -> ItemColor.VIOLET
    "YELLOW" -> ItemColor.YELLOW
    "RED" -> ItemColor.RED
    "PINK" -> ItemColor.PINK
    "GREEN" -> ItemColor.GREEN
    "BLUE" -> ItemColor.BLUE
    else -> {
        ItemColor.WHITE
    }
}