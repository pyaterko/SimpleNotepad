package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.notes

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.owl_laugh_at_wasted_time.domain.entity.ItemNote
import com.owl_laugh_at_wasted_time.notepad.NotesListRVAdapter
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.displayAConfirmationDialog
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.getColorString
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.showActionAlertDialog
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.FragmentListNotesBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.activity.MainActivity
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.BaseFragment
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.ReadTask
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.viewBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.read.ReadFragment
import com.owl_laugh_at_wasted_time.viewmodel.notes.NotesListViewModel
import java.io.File


class NotesListFragment : BaseFragment(R.layout.fragment_list_notes) {

    private lateinit var listNotes: List<ItemNote>
    private val binding by viewBinding(FragmentListNotesBinding::bind)
    private val viewModel by viewModels<NotesListViewModel> { viewModelFactory }
    private val adapter: NotesListRVAdapter by lazy { NotesListRVAdapter() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFabOnClickListener()
        binding.recyclerViewListNotes.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerViewListNotes.adapter = adapter
        viewModel.listNotes.collectWhileStarted {
            binding.noDataImageView.isVisible = it.size == 0
            adapter.notes = it
            listNotes = it.toList()
        }
        setupSwipe(binding.recyclerViewListNotes)
        adapter.onImageViewMoreVertListener = {
            val note = it.tag as ItemNote
            showNoteMenu(it, note.id)
        }

        adapter.onItemClickListener = {
            findNavController().navigate(
                R.id.action_notesListFragment_to_readFragment,
                bundleOf(
                    ReadFragment.CATEGORY to true,
                    ReadFragment.READ_KEY_TITLE to it.title,
                    ReadFragment.READ_KEY_TEXT to it.text
                ),
                navOptions {
                    anim {
                        enter = R.anim.enter
                        exit = R.anim.exit
                        popEnter = R.anim.pop_enter
                        popExit = R.anim.pop_exit
                    }
                }
            )
        }

        adapter.onNoteLongClickListener = {
            findNavController().navigate(
                R.id.action_notesListFragment_to_createNotesFragment,
                bundleOf(CreateNotesFragment.NOTES_ID to it.id),
                navOptions {
                    anim {
                        enter = R.anim.enter
                        exit = R.anim.exit
                        popEnter = R.anim.pop_enter
                        popExit = R.anim.pop_exit
                    }
                }
            )
        }
        setSearch()

    }

    private fun setSearch() {
        binding.searchNote.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchNote.onActionViewCollapsed()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val list = listNotes.filter { it.title.contains(newText, true) }
                adapter.notes = list.toList()
                return true
            }
        })
    }

    private fun setFabOnClickListener() {
        binding.buttonFabNotesList.setOnClickListener {
            showFabMenu(it)
        }
    }

    private fun setupSwipe(recyclerView: RecyclerView?) {
        val callBack = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                showDeleteAlertDialog(viewHolder)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }


    private fun showDeleteAlertDialog(
        viewHolder: RecyclerView.ViewHolder? = null,
        view: View? = null
    ) {
        var note: ItemNote? = null
        if (view == null) {
            if (viewHolder != null) {
                note = adapter.notes[viewHolder.adapterPosition]
            }
        } else {
            note = view.tag as ItemNote
        }
        displayAConfirmationDialog(requireContext(),
            getString(R.string.default_alert_message),
            actionPB1 = {
                if (note != null) {
                    launchScope {
                        viewModel.deleteNote(note)
                    }

                }
            },
            actionNB1 = {
                binding.recyclerViewListNotes.adapter = adapter
            }
        )
    }

    private fun showNoteMenu(view: View, id: Int) {
        val popupMenu = PopupMenu(view.context, view)
        val note = view.tag as ItemNote
        popupMenu.menu.add(0, EDITOR, Menu.NONE, view.context.getString(R.string.edit_note))
        popupMenu.menu.add(
            0,
            SAVE_FILE,
            Menu.NONE,
            view.context.getString(R.string.save_in_documents)
        )
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                EDITOR -> {
                    findNavController().navigate(
                        R.id.action_notesListFragment_to_createNotesFragment,
                        bundleOf(CreateNotesFragment.NOTES_ID to id),
                        navOptions {
                            anim {
                                enter = R.anim.enter
                                exit = R.anim.exit
                                popEnter = R.anim.pop_enter
                                popExit = R.anim.pop_exit
                            }
                        }
                    )
                }
                SAVE_FILE -> {
                    if (notPermision()) {
                        requestPermision()
                    } else {
                        showActionAlertDialog(
                            requireContext(),
                            layoutInflater,
                            getString(R.string.file_save),
                            getString(R.string.empty_value),
                            R.string.save,
                            R.string.name_of_file,
                            actionPB1 = { str ->
                                if (str.isNotBlank()) {
                                    viewModel.save(
                                        requireContext(),
                                        str,
                                        "<<id:${note.id}>>\n <<dateOfCreation:${note.dateOfCreation}>>\n <<color:${note.color?.getColorString()}>>\n <<title:${note.title}>>\n ${note.text}"
                                    )
                                }
                            }
                        )
                    }


                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    private fun showFabMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menu.add(
            0,
            EDITOR,
            Menu.NONE, view.context.getString(R.string.add_note)
        )
        popupMenu.menu.add(
            0,
            SAVE_FILE,
            Menu.NONE,
            view.context.getString(R.string.remote_from_file)
        )
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                EDITOR -> {
                    findNavController().navigate(
                        R.id.action_notesListFragment_to_createNotesFragment,
                        bundleOf(CreateNotesFragment.NOTES_ID to CreateNotesFragment.UNDEFINED_ID),
                        navOptions {
                            anim {
                                enter = R.anim.enter
                                exit = R.anim.exit
                                popEnter = R.anim.pop_enter
                                popExit = R.anim.pop_exit
                            }
                        }
                    )
                }
                SAVE_FILE -> {
                    openFile()
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    private fun requestPermision() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_WRITE_EXTERNAL_PERMISSION
        )
    }

    private fun notPermision() = ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) != PackageManager.PERMISSION_GRANTED


    private fun openFile() {
        val dirList: MutableList<String> = ArrayList()
        dirList.add(File.separator)
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = TEXT_WILD
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, OPEN_DOCUMENT)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_CANCELED) return
        when (requestCode) {
            OPEN_DOCUMENT -> {
                val content = data?.data
                content?.let {
                    val read = ReadTask(activity as MainActivity) {
                        viewModel.restoreNote(it, "")
                    }
                    read.execute(it)
                }
            }
        }
    }

    companion object {
        private const val TEXT_WILD = "text/*"
        private const val OPEN_DOCUMENT = 1
        private const val EDITOR = 1
        private const val SAVE_FILE = 2
        private const val REQUEST_WRITE_EXTERNAL_PERMISSION = 2

    }
}