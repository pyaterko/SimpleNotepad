package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.todo

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.provider.CalendarContract
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.owl_laugh_at_wasted_time.domain.*
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.getColorDrawable
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.listener
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.parsePriority
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.shakeAndVibrate
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.FragmentCreateTodoBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.activity.MainActivity
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.BaseFragment
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.viewBinding
import com.owl_laugh_at_wasted_time.viewmodel.todo.TodoListViewModel
import java.text.SimpleDateFormat
import java.util.*


class CreateToDoFragment : BaseFragment(R.layout.fragment_create_todo) {

    private var itemToDo = ItemToDo()
    private var showAnErrorInTheSelection = false
    private val binding by viewBinding(FragmentCreateTodoBinding::bind)
    private val viewModel by viewModels<TodoListViewModel> { viewModelFactory }
    private val args: CreateToDoFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchScope {
            val id = args.todoId
            if (id != UNDEFINED_ID) {
                itemToDo = viewModel.getNoteById(id)
            }
            setData(binding.todoTitle, binding.todoText)
        }

        binding.colorPicturesToDo.onColorClickListener = {
            itemToDo.color = it
            binding.indicatorColor.setBackgroundTintList(
                ColorStateList.valueOf(
                    it.getColorDrawable(
                        requireContext()
                    )
                )
            )
            closePalette()
        }
        binding.currentPrioritiesSpinner.onItemSelectedListener = listener(requireContext()) {
            binding.textView.setTextColor(resources.getColor(R.color.black))
            showError()
        }

        binding.indicatorColor.setOnClickListener {
            openPalette()
        }

        setToolBarMenu(
            blockCreateMenu = {
                val menuItemAlarm = it.findItem(R.id.menu_alarm)
                menuItemAlarm.setVisible(true)
                val menuItemSave = it.findItem(R.id.menu_save)
                menuItemSave.setVisible(true)
            },
            blockMenuItemSelected = {
                when (it.itemId) {
                    R.id.menu_alarm -> {
                        checkBeforeIvent { _, _, _ ->
                            setReminder()
                        }
                    }
                    R.id.menu_save -> {
                        checkBeforeIvent { title, description, getPriority ->
                            itemToDo.title = title
                            itemToDo.text = description
                            itemToDo.priority = parsePriority(getPriority)
                            launchScope {
                                viewModel.addItemNote(itemToDo)
                            }
                            findNavController().navigateUp()
                        }
                    }
                }
            })
    }

    private fun setReminder() {
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
                var month: String = SimpleDateFormat(
                    TIME_STRING_MONTH,
                    Locale.getDefault()
                ).format(Date(dateRangePicker.selection!!))
                val year: String = SimpleDateFormat(
                    TIME_STRING_YEAR,
                    Locale.getDefault()
                ).format(Date(dateRangePicker.selection!!))
                val day: String = SimpleDateFormat(
                    TIME_STRING_DAY,
                    Locale.getDefault()
                ).format(Date(dateRangePicker.selection!!))
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_MONTH, day.toInt())
                calendar.set(Calendar.YEAR, year.toInt())
                calendar.set(Calendar.MONTH, month.toInt() - 1)
                calendar.set(Calendar.HOUR_OF_DAY, materialTimePicker.hour)
                calendar.set(Calendar.MINUTE, materialTimePicker.minute)

                val text = SimpleDateFormat(
                    DATE_TIME_FORMAT,
                    Locale.getDefault()
                )
                    .format(calendar.time)
                itemToDo.data = text
                itemToDo.title = binding.todoTitle.text.toString()
                itemToDo.text = binding.todoText.text.toString()
                itemToDo.priority =
                    parsePriority(binding.currentPrioritiesSpinner.selectedItem.toString())
                if (itemToDo.id != UNDEFINED_ID) {
                    launchScope {
                        viewModel.addItemNote(itemToDo)
                    }
                }
                addEvent(itemToDo.title, calendar.time.time)
            }
        }
    }

    private fun addEvent(title: String, begin: Long) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
        }
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).binding.selectContainerCard.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).binding.selectContainerCard.visibility = View.GONE
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                showAnErrorInTheSelection = false
                binding.textView2.setTextColor(resources.getColor(R.color.black))
                showError()
            }

            override fun afterTextChanged(sequence: Editable?) {}
        }
        binding.todoTitle.addTextChangedListener(textWatcher)
    }

    private fun isShowAnErrorInTheSelection(title: Boolean, priorety: Boolean) {
        if (showAnErrorInTheSelection) {
            when (title) {
                true -> {
                    binding.llError.visibility = View.GONE
                    binding.textView2.setTextColor(resources.getColor(R.color.black))
                }
                false -> {
                    binding.llError.visibility = View.VISIBLE
                    binding.textView2.setTextColor(resources.getColor(R.color.color_red))
                    binding.textView2.shakeAndVibrate()
                }
            }
            when (priorety) {
                true -> {

                    if (title) binding.llError.visibility = View.GONE
                    binding.textView.setTextColor(resources.getColor(R.color.black))
                }
                false -> {
                    binding.textView.setTextColor(resources.getColor(R.color.color_red))
                    binding.textView.shakeAndVibrate()
                    if (title) binding.llError.visibility = View.VISIBLE
                }
            }
        }
        showAnErrorInTheSelection = false
    }

    private fun verifyTitleFromUser(title: String): Boolean {
        return (title.isNotEmpty())
    }

    private fun verifyPrioretyFromUser(priorety: String): Boolean {
        return !(priorety.equals("Выберите приоритет")) && !(priorety.equals("Choose a priority"))
    }

    private fun showError() {
        if ((binding.todoTitle.text.toString().isNotEmpty()) &&
            !(binding.currentPrioritiesSpinner.selectedItem.toString()).equals("Выберите приоритет")
        ) {
            binding.llError.visibility = View.GONE
        }
    }

    private fun checkBeforeIvent(ivent: (String, String, String) -> Unit) {
        val title = binding.todoTitle.text.toString()
        val description = binding.todoText.text.toString()
        val getPriority = binding.currentPrioritiesSpinner.selectedItem.toString()
        if (verifyTitleFromUser(title) &&
            verifyPrioretyFromUser(getPriority)
        ) {
            ivent.invoke(title, description, getPriority)
        } else {
            showAnErrorInTheSelection = true
            isShowAnErrorInTheSelection(
                verifyTitleFromUser(title),
                verifyPrioretyFromUser(getPriority)
            )
        }
    }

    private fun setData(title: EditText, text: EditText) {
        title.setText(itemToDo.title)
        text.setText(itemToDo.text)
        setColorInIndicator(itemToDo.color.getColorDrawable(requireContext()))
    }

    private fun closePalette() {
        if (binding.colorPicturesToDo.isOpen) {
            binding.colorPicturesToDo.close()
        }
    }

    private fun openPalette() {
        if (!(binding.colorPicturesToDo.isOpen)) {
            binding.colorPicturesToDo.open()
        }
    }

    private fun setColorInIndicator(color: Int) {
        binding.indicatorColor.setBackgroundTintList(ColorStateList.valueOf(color))
    }
}