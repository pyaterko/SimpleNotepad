package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.todo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.elveum.elementadapter.SimpleBindingAdapter
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.domain.entity.SubTaskItem
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.displayAConfirmationDialog
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.preferences
import com.owl_laugh_at_wasted_time.settings.activity.SettingsActivity
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.FragmentListTodoBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.BaseFragment
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.callback.TouchHelperCallback
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.decorator.ItemDecoration
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.viewBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.adapters.OnToDoListener
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.adapters.createToDoAdapter
import com.owl_laugh_at_wasted_time.viewmodel.todo.TodoListViewModel
import java.util.*


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

        viewModel.liveData.observe(viewLifecycleOwner) {
            binding.noDataImageView.isVisible = it.size == 0
            adapter.submitList(it.toList().sortedBy { it.done })
            listToDo = it.toList().sortedBy { it.done }
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

        fabActionOnScroll(
            binding.recyclerViewListToDo,
            binding.buttonFabToDoList,
            null,
            null
        )

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
                itemToDo.id
            )
        launchFragment(directions)
    }

    override fun markAsDoneToDo(itemToDo: ItemToDo) {
        deleteNotification(itemToDo.id.hashCode(), {})
        viewModel.addToDo(itemToDo.copy(done = !itemToDo.done))
    }

    override fun markAsDone(item: SubTaskItem) {
        viewModel.addSubTask(item.copy(done = !item.done))
    }

    override fun deleteItem(item: SubTaskItem) {
        viewModel.deleteItemById(item.text)
    }

    override fun getSubTaskList(
        adapter: SimpleBindingAdapter<SubTaskItem>,
        itemToDo: ItemToDo,
        textView: TextView
    ) {
        viewModel.flowSubTask(itemToDo.id!!).collectWhileStarted {
            adapter.submitList(it.sortedBy { it.done })
            val tasks = it.size
            var tasksCompleted = 0
            for (item in it) {
                if (item.done) tasksCompleted++
            }
            textView.text = "$tasksCompleted/$tasks"
        }
    }

    override fun showSubTasks(view: View) {
        if (view.isVisible) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
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
                ToDoListFragmentDirections.actionToDoListFragmentToCreateToDoFragment()
            launchFragment(directions)

        }
    }

    private fun showDeleteAlertDialog(
        itemId: UUID? = null,
        view: View? = null
    ) {
        val itemToDo: ItemToDo?
        val id = if (view == null) {
            itemId
        } else {
            itemToDo = view.tag as ItemToDo
            itemToDo.id
        }
        displayAConfirmationDialog(requireContext(),
            getString(R.string.default_alert_message),
            {
                deleteNotification(id!!.hashCode()) {
                    viewModel.deleteItem(id)
                    viewModel.deleteSubTask(id)
                }
            },
            { binding.recyclerViewListToDo.adapter?.notifyDataSetChanged() }
        )
    }

}