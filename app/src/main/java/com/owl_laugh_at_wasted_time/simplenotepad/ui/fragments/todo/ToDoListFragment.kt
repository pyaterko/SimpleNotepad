package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.todo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.domain.entity.PriorityToDo
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.displayAConfirmationDialog
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.FragmentListTodoBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.BaseFragment
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.viewBinding
import com.owl_laugh_at_wasted_time.viewmodel.todo.TodoListViewModel
import java.text.SimpleDateFormat
import java.util.*


class ToDoListFragment : BaseFragment(R.layout.fragment_list_todo) {


    private lateinit var listToDo: List<ItemToDo>
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

        setupSwipe(binding.recyclerViewListToDo)

        adapter.onImageViewMoreVertListener = {
            showToDoMenu(it)
        }

        adapter.onItemClickListener = {
            val directions =
                ToDoListFragmentDirections.actionToDoListFragmentToCreateToDoFragment(it.id)
            launchFragment(directions)
        }
        setSearch()
    }

    private fun showToDoMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val toDoData = view.tag as ItemToDo
        popupMenu.menu.add(0, REMINDER, Menu.NONE, view.context.getString(R.string.reminder))
        popupMenu.menu.add(
            0,
            DELETE_TO_DO,
            Menu.NONE,
            view.context.getString(R.string.delete_event)
        )
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                REMINDER -> {
                    if (toDoData.priority == PriorityToDo.HIGH) {
                        setReminder(view)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "измените приоритет мероприятия на ВЫСОКИЙ",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                DELETE_TO_DO -> {
                    showDeleteAlertDialog(view = view)
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    private fun setReminder(view: View) {
        val toDoItem = view.tag as ItemToDo
        val dateRangePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Выберите дату для напоминания")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        dateRangePicker.show(parentFragmentManager, "TAGTAG")
        dateRangePicker.addOnPositiveButtonClickListener {
            val materialTimePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(0)
                .setMinute(0)
                .setTitleText("Выберите время для напоминания")
                .build()
            materialTimePicker.show(parentFragmentManager, "TAG")
            materialTimePicker.addOnPositiveButtonClickListener {
                val date = Date(dateRangePicker.selection!!)
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.DATE, date.date)
                calendar.set(Calendar.HOUR_OF_DAY, materialTimePicker.hour)
                calendar.set(Calendar.MINUTE, materialTimePicker.minute)

                val text = SimpleDateFormat(
                    "dd.MM.YYYY HH:mm",
                    Locale.getDefault()
                )
                    .format(calendar.time)
                val updatedItem = toDoItem.copy(data = text)
                launchScope {
                    viewModel.addItemNote(updatedItem)
                }
                addEvent("НАПОМИНАНИЕ: ${toDoItem.title}", calendar.time.time)
            }
        }
    }

    fun addEvent(title: String, begin: Long) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
        }
        startActivity(intent)

    }
    
    private fun setSearch() {
        binding.searchToDo.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchToDo.onActionViewCollapsed()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val list = listToDo.filter { it.title.contains(newText, true) }
                adapter.list = list.toList()
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

    private fun launchFragment(directions: NavDirections) {
        findNavController().navigate(
            directions,
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
                binding.recyclerViewListToDo.adapter = adapter
            }
        )
    }

    companion object {
        private const val REMINDER = 1
        private const val DELETE_TO_DO = 2
    }
}