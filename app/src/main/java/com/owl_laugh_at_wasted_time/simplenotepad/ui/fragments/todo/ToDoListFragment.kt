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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.elveum.elementadapter.SimpleBindingAdapter
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.displayAConfirmationDialog
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.getId
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.preferences
import com.owl_laugh_at_wasted_time.settings.activity.SettingsActivity
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.FragmentListTodoBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.BaseFragment
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.TouchHelperCallback
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.decorator.ItemDecoration
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.viewBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.adapters.OnToDoListener
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.adapters.createToDoAdapter
import com.owl_laugh_at_wasted_time.viewmodel.todo.TodoListViewModel


class ToDoListFragment : BaseFragment(R.layout.fragment_list_todo), OnToDoListener {


    private var listToDo: List<ItemToDo>? = null
    private val binding by viewBinding(FragmentListTodoBinding::bind)
    private val viewModel by viewModels<TodoListViewModel> { viewModelFactory }
    private lateinit var adapter: SimpleBindingAdapter<ItemToDo>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFabOnClickListener()
        binding.recyclerViewListToDo.layoutManager =
            LinearLayoutManager(requireContext())
        adapter = createToDoAdapter(requireContext(), this)
        binding.recyclerViewListToDo.adapter = adapter
        binding.recyclerViewListToDo.isNestedScrollingEnabled = false
        val dividerItemDecoration = ItemDecoration(16)
        binding.recyclerViewListToDo.addItemDecoration(dividerItemDecoration)
        viewModel.flow.collectWhileStarted {
            binding.noDataImageView.isVisible = it.size == 0
            adapter.submitList(it)
            listToDo = it.toList()
        }
        if (preferences(requireContext()).getBoolean(
                getString(R.string.settings_swipe_to_trash_key),
                true
            )
        ) {
            val touchCallback =
                TouchHelperCallback(adapter) { itemToDo -> showDeleteAlertDialog(itemToDo.id) }
            val touchHelper = ItemTouchHelper(touchCallback)
            touchHelper.attachToRecyclerView(binding.recyclerViewListToDo)
        }

        setToolBarMenu(blockCreateMenu = {
            val settings = it.findItem(R.id.menu_settings)
            settings?.setVisible(true)
        }, {
            when (it.itemId) {
                R.id.menu_settings -> {
                    val settingsIntent = Intent(requireContext(), SettingsActivity::class.java)
                    startActivity(settingsIntent)
                }
            }
        })

        setSearch()
    }

    override fun launchToCreateToDoFragment(itemToDo: ItemToDo) {
        val directions =
            ToDoListFragmentDirections.actionToDoListFragmentToCreateToDoFragment(
                itemToDo.priority.getId(),
                itemToDo.id
            )
        launchFragment(directions)
    }

    override fun showMenu(view: View) {
        showToDoMenu(view)
    }

    private fun showToDoMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menu.add(
            0,
            DELETE_TO_DO,
            Menu.NONE,
            view.context.getString(R.string.delete_event)
        )
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
                adapter.submitList(list?.toList() ?: emptyList())
                return true
            }
        })
    }

    private fun setFabOnClickListener() {
        binding.buttonFabToDoList.setOnClickListener {
            val directions =
                ToDoListFragmentDirections.actionToDoListFragmentToCreateToDoFragment(0, 0)
            launchFragment(directions)

        }
    }

    private fun showDeleteAlertDialog(
        itemId: Int = 0,
        view: View? = null
    ) {
        val note: ItemToDo?
        val id = if (view == null) {
            itemId
        } else {
            note = view.tag as ItemToDo
            note.id
        }
        displayAConfirmationDialog(requireContext(),
            getString(R.string.default_alert_message),
           { deleteNotification(id, {}) { viewModel.deleteNote(id) } },
            { binding.recyclerViewListToDo.adapter?.notifyDataSetChanged() }
        )
    }

    companion object {
        private const val DELETE_TO_DO = 2
    }
}