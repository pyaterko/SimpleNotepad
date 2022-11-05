package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.todo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.displayAConfirmationDialog
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.FragmentListTodoBinding
import com.owl_laugh_at_wasted_time.settings.activity.SettingsActivity
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.BaseFragment
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.viewBinding
import com.owl_laugh_at_wasted_time.viewmodel.todo.TodoListViewModel


class ToDoListFragment : BaseFragment(R.layout.fragment_list_todo) {


    private var listToDo: List<ItemToDo>? = null
    private val binding by viewBinding(FragmentListTodoBinding::bind)
    private val viewModel by viewModels<TodoListViewModel> { viewModelFactory }
    private val adapter: ToDoListRVAdapter by lazy { ToDoListRVAdapter() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFabOnClickListener()
        binding.recyclerViewListToDo.layoutManager =
            LinearLayoutManager(requireContext())
        binding.recyclerViewListToDo.adapter = adapter
        viewModel.listNotes.collectWhileStarted {
            binding.noDataImageView.isVisible = it.size == 0
            adapter.list = it
            listToDo = it.toList()
        }
        setupSwipe(binding.recyclerViewListToDo, block = {
            showDeleteAlertDialog(it)
        })

        adapter.onImageViewMoreVertListener = {
            showToDoMenu(it)
        }

        adapter.onItemClickListener = {
            val directions =
                ToDoListFragmentDirections.actionToDoListFragmentToCreateToDoFragment(it.id)
            launchFragment(directions)
        }

        setToolBarMenu(blockCreateMenu = {
            val settings = it.findItem(R.id.menu_settings)
            settings?.setVisible(true)
        }, blockMenuItemSelected = {
            when (it.itemId) {
                R.id.menu_settings -> {
                    val settingsIntent = Intent(requireContext(), SettingsActivity::class.java)
                    startActivity(settingsIntent)
                }
            }
        })

        setSearch()
    }

    private fun showToDoMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menu.add(0, DELETE_TO_DO, Menu.NONE, view.context.getString(R.string.delete_event))
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                DELETE_TO_DO -> {
                    showDeleteAlertDialog(view = view)
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    private fun setSearch() {
        binding.searchToDo.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchToDo.onActionViewCollapsed()
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                val list = listToDo?.filter { it.title.contains(newText, true) }
                adapter.list = list?.toList() ?: emptyList()
                return true
            }
        })
    }

    private fun setFabOnClickListener() {
        binding.buttonFabToDoList.setOnClickListener {
            val directions =
                ToDoListFragmentDirections.actionToDoListFragmentToCreateToDoFragment()
            launchFragment(directions)

        }
    }

    private fun showDeleteAlertDialog(
        viewHolder: RecyclerView.ViewHolder? = null,
        view: View? = null
    ) {
        var note: ItemToDo? = null
        if (view == null) {
            if (viewHolder != null) {
                note = adapter.list[viewHolder.adapterPosition]
            }
        } else {
            note = view.tag as ItemToDo
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
                binding.recyclerViewListToDo.adapter?.notifyDataSetChanged()
            }
        )
    }

    companion object {
        private const val DELETE_TO_DO = 2
    }
}