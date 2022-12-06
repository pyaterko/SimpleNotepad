package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.notes

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.elveum.elementadapter.SimpleBindingAdapter
import com.getbase.floatingactionbutton.AddFloatingActionButton
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.google.android.material.tabs.TabLayout
import com.owl_laugh_at_wasted_time.domain.entity.ItemCategory
import com.owl_laugh_at_wasted_time.domain.entity.ItemNote
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.displayAConfirmationDialog
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.getColorString
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.preferences
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.showActionAlertDialog
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.FragmentListNotesBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.activity.MainNoteBookActivity
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.BaseFragment
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.ReadTask
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.callback.SwipeHelper
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.decorator.ItemDecoration
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.viewBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.adapters.OnClickCategory
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.adapters.OnNoteListener
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.adapters.createListNotesCategoryAdapter
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.adapters.createNotesAdapter
import com.owl_laugh_at_wasted_time.viewmodel.notes.NotesListViewModel
import java.io.File


class NotesListFragment : BaseFragment(R.layout.fragment_list_notes), OnNoteListener,
    OnClickCategory {

    private lateinit var listNotes: List<ItemNote>
    private val binding by viewBinding(FragmentListNotesBinding::bind)
    private val viewModel by viewModels<NotesListViewModel> { viewModelFactory }
    private lateinit var adapter: SimpleBindingAdapter<ItemNote>
    private lateinit var adapterCategory: SimpleBindingAdapter<ItemCategory>
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
        fabActionOnScroll(binding.recyclerViewListNotes, null, {
            mActionsMenu.isVisible = true
            collapseFab()
        }, {
            collapseFab()
            mActionsMenu.isVisible = false
        })

        adapter = createNotesAdapter(requireContext(), this)
        adapterCategory = createListNotesCategoryAdapter(this)
        binding.rvListNotesCategory.adapter = adapterCategory
        binding.recyclerViewListNotes.adapter = adapter
        binding.recyclerViewListNotes.isNestedScrollingEnabled = false
        val dividerItemDecoration = ItemDecoration(16)
        binding.recyclerViewListNotes.addItemDecoration(dividerItemDecoration)
        viewModel.categoryList.observe(viewLifecycleOwner) {
            adapterCategory.submitList(it)
        }
        viewModel.listNotes.collectWhileStarted {
            binding.noDataImageView.isVisible = it.size == 0
            adapter.submitList(it)
            listNotes = it.toList()
        }
        ItemTouchHelper(object : SwipeHelper(
            requireContext(),
            binding.recyclerViewListNotes,
            { !preferences(requireContext()).getBoolean(CURRENT_BOOLEAN, true) }
        ) {
            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder?,
                underlayButtons: MutableList<UnderlayButton>?
            ) {
                viewHolder?.let {
                    underlayButtons?.add(deleteButton(adapter.currentList[it.adapterPosition]))
                    underlayButtons?.add(editorButton(adapter.currentList[it.adapterPosition]))
                    if (preferences(requireContext()).getBoolean(
                            getString(R.string.settings_import_data_key),
                            true
                        )
                    ) {
                        underlayButtons?.add(saveFileButton(adapter.currentList[it.adapterPosition]))
                    }
                }
            }
        })
        setSearch()
        setToolBarMenu(
            {
                val menuItemStream = it.findItem(R.id.menu_view_stream)
                val menuItemGrid = it.findItem(R.id.menu_grid_view)
                menuItemStream?.setVisible(true)
                menuItemGrid?.setVisible(true)
            }, {
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
                adapter.submitList(list.toList())
                return true
            }
        })
    }

    private fun setFabOnClickListener() {
        initFab()
    }

    private fun showDeleteAlertDialog(
        itemId: Int = 0,
        view: View? = null
    ) {
        var note: ItemNote? = null
        val id = if (view == null) {
            itemId
        } else {
            note = view.tag as ItemNote
            note.id
        }
        displayAConfirmationDialog(requireContext(),
            getString(R.string.default_alert_message),
            actionPB1 = {
                deleteNote(id)
            },
            actionNB1 = {
                binding.recyclerViewListNotes.adapter?.notifyDataSetChanged()
            }
        )
    }

    private fun deleteButton(note: ItemNote): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            true,
            "Удалить",
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_delete_outline_24
            ),
            Color.parseColor("#ffff4444"), Color.parseColor("#ffffff")
        ) {
            showDeleteAlertDialog(note.id)
        }
    }


    private fun editorButton(note: ItemNote): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            true,
            "Редактор",
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_create
            ),
            Color.parseColor("#ff9800"), Color.parseColor("#ffffff")
        ) {
            val directions =
                NotesListFragmentDirections.actionNotesListFragmentToCreateNotesFragment(note.id)
            launchFragment(directions)
        }
    }

    private fun saveFileButton(note: ItemNote): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            true,
            "Файл",
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_file_document_black_48dp
            ),
            Color.parseColor("#33CAAC"), Color.parseColor("#ffffff")
        ) {
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
            adapter.notifyItemChanged(it)
        }
    }

    private fun showNoteMenu(view: View, id: Int) {
        val popupMenu = PopupMenu(view.context, view)
        val note = view.tag as ItemNote
        popupMenu.menu.add(0, DELETE, Menu.NONE, view.context.getString(R.string.delete_note))
        popupMenu.menu.add(0, EDITOR, Menu.NONE, view.context.getString(R.string.edit_note))
        if (preferences(requireContext()).getBoolean(
                getString(R.string.settings_import_data_key),
                true
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
                    showDeleteAlertDialog(0, view)
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

    private fun deleteNote(itemId: Int) {
        launchScope {
            viewModel.deleteNote(itemId)
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
                    true
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
        const val CURRENT_BOOLEAN = "CURRENT_BOOLEAN"
    }

    override fun launchToReadFragment(itemNote: ItemNote) {
        collapseFab()
        binding.searchNote.onActionViewCollapsed()
        val directions =
            NotesListFragmentDirections.actionNotesListFragmentToReadFragment(
                true, itemNote.title, itemNote.text
            )
        launchFragment(directions)
    }

    override fun launchToCreateNotesFragment(itemNote: ItemNote) {
        collapseFab()
        binding.searchNote.onActionViewCollapsed()
        val directions =
            NotesListFragmentDirections.actionNotesListFragmentToCreateNotesFragment(itemNote.id)
        launchFragment(directions)
    }

    override fun showMenu(view: View, itemNote: ItemNote) {
        collapseFab()
        showNoteMenu(view, itemNote.id)
    }

    override fun onClickCategoryItem(item: ItemCategory) {
        viewModel.updateCategory(item)
    }

}