package com.owl_laugh_at_wasted_time.viewmodel.notes

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owl_laugh_at_wasted_time.domain.entity.ItemColor
import com.owl_laugh_at_wasted_time.domain.entity.ItemNote
import com.owl_laugh_at_wasted_time.domain.repository.CategorysRepository
import com.owl_laugh_at_wasted_time.domain.repository.NoteRepository
import com.owl_laugh_at_wasted_time.domain.repository.UiActions
import com.owl_laugh_at_wasted_time.viewmodel.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.*
import java.util.regex.Pattern
import javax.inject.Inject


class NotesListViewModel @Inject constructor(
    private val uiActions: UiActions,
    private val repositoryNote: NoteRepository,
    private val categorysRepository: CategorysRepository
) : ViewModel() {

    val listNotes = repositoryNote.getLiveDate()
    val categoriesLiveData = categorysRepository.getAllData()


    init {
        if (uiActions.isPopulateInitialData()) {
            viewModelScope.launch {
                delay(1000)
                categorysRepository.populateInitialData()
            }
            uiActions.notPopulateInitialData()
        }
    }

    fun listCategorys(category: String) =
        repositoryNote.getItemsByCategory(category)


    suspend fun getNoteById(noteId: Int) =
        repositoryNote.getById(noteId)


    suspend fun addItemNote(item: ItemNote) {
        repositoryNote.add(item)
    }

    suspend fun deleteNote(itemId: Int) {
        repositoryNote.delete(itemId)
    }


    fun save(context: Context, fileName: String, text: String) {
        viewModelScope.launch {
            saveFile(context, fileName, text)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun saveFile(context: Context, fileName: String, text: String) {
        try {
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
            Toast.makeText(
                context,
                "${context.getString(R.string.success_save)} $fileName",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(context, "некорректное имя файла", Toast.LENGTH_LONG).show()
        }
    }

    fun openFile(name: String, context: Context) {
        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val dir = File("${root.absolutePath}/")
        dir.mkdirs()
        val f2 = File(dir, "$name.txt")
        try {
            val line = FileInputStream(f2).bufferedReader().use { it.readText() }
            val nameFile = restoreNote(line, name)
            Toast.makeText(context, "ВОССТАНОВЛЕНО $nameFile", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Toast.makeText(context, context.getString(R.string.not_file), Toast.LENGTH_LONG).show()
        }

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