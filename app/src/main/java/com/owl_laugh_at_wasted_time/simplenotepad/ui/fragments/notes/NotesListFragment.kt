package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.notes

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.getbase.floatingactionbutton.AddFloatingActionButton
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.google.android.material.tabs.TabLayout
import com.owl_laugh_at_wasted_time.domain.entity.ItemNote
import com.owl_laugh_at_wasted_time.notepad.NotesListRVAdapter
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.displayAConfirmationDialog
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.getColorString
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.preferences
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.showActionAlertDialog
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.FragmentListNotesBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.activity.MainNoteBookActivity
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.BaseFragment
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.ReadTask
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.viewBinding
import com.owl_laugh_at_wasted_time.viewmodel.notes.NotesListViewModel
import java.io.File


class NotesListFragment : BaseFragment(R.layout.fragment_list_notes) {

    private lateinit var listNotes: List<ItemNote>
    private val binding by viewBinding(FragmentListNotesBinding::bind)
    private val viewModel by viewModels<NotesListViewModel> { viewModelFactory }
    private val adapter: NotesListRVAdapter by lazy { NotesListRVAdapter() }
    private lateinit var mActionsMenu: FloatingActionsMenu

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val tab: TabLayout.Tab? =
                        (activity as MainNoteBookActivity).binding.selectTabs.getTabAt(0)
                    tab?.select()
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActionsMenu = binding.fab.fab
        setFabOnClickListener()
        setRVLaoutManager()
        binding.recyclerViewListNotes.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        collapseFab()
                        mActionsMenu.isVisible = false

                    } else if (dy < 0) {
                        mActionsMenu.isVisible = true
                        collapseFab()
                    } else {
                        Log.d("TAG", "No Vertical Scrolled")
                    }
                }
            }
        )
        binding.recyclerViewListNotes.adapter = adapter
        viewModel.listNotes.collectWhileStarted {
            binding.noDataImageView.isVisible = it.size == 0
            adapter.notes = it
            listNotes = it.toList()
        }

        setupSwipe(binding.recyclerViewListNotes, block = {
            showDeleteAlertDialog(it)
        })

        adapter.onImageViewMoreVertListener = {
            collapseFab()
            val note = it.tag as ItemNote
            showNoteMenu(it, note.id)
        }

        adapter.onItemClickListener = {
            collapseFab()
            binding.searchNote.onActionViewCollapsed()
            val directions =
                NotesListFragmentDirections.actionNotesListFragmentToReadFragment(
                    true, it.title, it.text
                )
            launchFragment(directions)
        }

        adapter.onNoteLongClickListener = {
            collapseFab()
            binding.searchNote.onActionViewCollapsed()
            val directions =
                NotesListFragmentDirections.actionNotesListFragmentToCreateNotesFragment(it.id)
            launchFragment(directions)
        }
        setSearch()
        setToolBarMenu(
            blockCreateMenu = {
                val menuItemStream = it.findItem(R.id.menu_view_stream)
                val menuItemGrid = it.findItem(R.id.menu_grid_view)
                menuItemStream?.setVisible(true)
                menuItemGrid?.setVisible(true)
            },
            blockMenuItemSelected = {
                when (it.itemId) {
                    R.id.menu_view_stream -> {
                        preferences(requireContext()).edit()
                            .putBoolean(CURRENT_BOOLEAN, false).apply()
                        setRVLaoutManager()
                    }
                    R.id.menu_grid_view -> {
                        preferences(requireContext()).edit()
                            .putBoolean(CURRENT_BOOLEAN, true).apply()
                        setRVLaoutManager()
                    }
                }
            })
    }

    private fun setRVLaoutManager() {
        if (preferences(requireContext()).getBoolean(CURRENT_BOOLEAN, true)) {
            binding.recyclerViewListNotes.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        } else {
            binding.recyclerViewListNotes.layoutManager =
                LinearLayoutManager(requireContext())
        }
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
        initFab()
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
                    deleteNote(note)
                }
            },
            actionNB1 = {
                binding.recyclerViewListNotes.adapter?.notifyDataSetChanged()
            }
        )
    }

    private fun showNoteMenu(view: View, id: Int) {
        val popupMenu = PopupMenu(view.context, view)
        val note = view.tag as ItemNote
        popupMenu.menu.add(0, DELETE, Menu.NONE, view.context.getString(R.string.delete_note))
        popupMenu.menu.add(0, EDITOR, Menu.NONE, view.context.getString(R.string.edit_note))
        if (preferences(requireContext()).getBoolean(
                getString(R.string.settings_import_data_key),
                false
            )
        ) {
            popupMenu.menu.add(
                0,
                SAVE_FILE,
                Menu.NONE,
                view.context.getString(R.string.save_in_documents)
            )
        }
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                DELETE -> {
                    showDeleteAlertDialog(null, view)
                }
                EDITOR -> {
                    val directions =
                        NotesListFragmentDirections.actionNotesListFragmentToCreateNotesFragment(id)
                    launchFragment(directions)
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

    private fun deleteNote(note: ItemNote) {
        launchScope {
            viewModel.deleteNote(note)
        }
    }

    private fun initFab() {
        val fabAddButton: AddFloatingActionButton = mActionsMenu
            .findViewById(com.getbase.floatingactionbutton.R.id.fab_expand_menu_button)

        fabAddButton.setOnLongClickListener {
            mActionsMenu.toggle()
            return@setOnLongClickListener true
        }

        fabAddButton.setOnClickListener {
            if (mActionsMenu.isExpanded) {
                mActionsMenu.collapse()
                return@setOnClickListener
            }
            if (preferences(requireContext()).getBoolean(
                    getString(R.string.settings_fab_expansion_behavior_key),
                    false
                )
            ) {
                val directions =
                    NotesListFragmentDirections.actionNotesListFragmentToCreateNotesFragment()
                launchFragment(directions)
            } else {
                mActionsMenu.expand()
            }
            setOnItemsFabListener()
        }
    }

    private fun setOnItemsFabListener() {
        binding.fab.fabNote.setOnClickListener {
            collapseFab()
            val directions =
                NotesListFragmentDirections.actionNotesListFragmentToCreateNotesFragment()
            launchFragment(directions)
        }

        if (preferences(requireContext()).getBoolean(
                getString(R.string.settings_import_data_key),
                false
            )
        ) {
            binding.fab.fabRemoteFromFile.visibility = View.VISIBLE
            binding.fab.fabRemoteFromFile.setOnClickListener {
                collapseFab()
                openFile()
            }
        } else {
            binding.fab.fabRemoteFromFile.visibility = View.GONE
        }
    }

    private fun collapseFab() {
        val mActionsMenu = binding.fab.fab
        if (mActionsMenu.isExpanded()) {
            mActionsMenu.collapse();
        }
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
                    val read = ReadTask(activity as MainNoteBookActivity) {
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
        private const val DELETE = 0
        private const val EDITOR = 1
        private const val SAVE_FILE = 2
        private const val REQUEST_WRITE_EXTERNAL_PERMISSION = 2
        private const val CURRENT_BOOLEAN = "CURRENT_BOOLEAN"

    }
}