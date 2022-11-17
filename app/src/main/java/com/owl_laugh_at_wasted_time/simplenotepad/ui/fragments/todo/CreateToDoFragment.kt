package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.todo

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.provider.CalendarContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.owl_laugh_at_wasted_time.domain.DATE_TIME_FORMAT
import com.owl_laugh_at_wasted_time.domain.TIME_STRING_DAY
import com.owl_laugh_at_wasted_time.domain.TIME_STRING_MONTH
import com.owl_laugh_at_wasted_time.domain.TIME_STRING_YEAR
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.getColorDrawable
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.hideKeyboard
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.preferences
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.shakeAndVibrate
import com.owl_laugh_at_wasted_time.settings.R.string.show_notifications_key
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.FragmentCreateTodoBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.activity.MainNoteBookActivity
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.BaseFragment
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.viewBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.notification.NotificationHelper
import com.owl_laugh_at_wasted_time.viewmodel.todo.TodoListViewModel
import java.text.SimpleDateFormat
import java.util.*


class CreateToDoFragment : BaseFragment(R.layout.fragment_create_todo) {

    private var itemToDo = ItemToDo()
    private var idItemToDo: UUID? = null
    private var showAnErrorInTheSelection = false
    private val binding by viewBinding(FragmentCreateTodoBinding::bind)
    private val viewModel by viewModels<TodoListViewModel> { viewModelFactory }
    private val args: CreateToDoFragmentArgs by navArgs()
    private var allEds: ArrayList<View> = arrayListOf()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
        idItemToDo = args.todoId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.todoTitle.requestFocus()
        launchScope {
            if (idItemToDo != null) {
                itemToDo = viewModel.getNoteById(idItemToDo!!)
            }
            setData(binding.todoTitle)
        }
        setOnClickListeners()
        initMenu()
        val linear = binding.linear
        binding.tvAdd.setOnClickListener {
            addSubTask(linear)
        }
    }

    private fun addSubTask(linear: LinearLayout) {
        val viewSub: View = layoutInflater.inflate(R.layout.custom_edittext_layout, null)
        val deleteField: TextView = viewSub.findViewById(R.id.button2) as TextView
        deleteField.setOnClickListener {
            try {
                (viewSub.parent as LinearLayout).removeView(viewSub)
                allEds.remove(viewSub)
            } catch (ex: IndexOutOfBoundsException) {
                ex.printStackTrace()
            }
        }
        allEds.add(viewSub)
        linear.addView(viewSub)
    }

    private fun setOnClickListeners() {
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
        binding.indicatorColor.setOnClickListener {
            openPalette()
        }
    }

    private fun initMenu() {
        setToolBarMenu(
            blockCreateMenu = {
                if (preferences(requireContext()).getBoolean(
                        getString(
                            show_notifications_key
                        ), true
                    )
                ) {
                    if (itemToDo.data == "") {
                        val menuItemAlarm = it.findItem(R.id.menu_alarm)
                        menuItemAlarm.setVisible(true)
                    } else {
                        val notificationOff = it.findItem(R.id.menu_notifications_off)
                        notificationOff.setVisible(true)
                    }
                }

                val menuItemSave = it.findItem(R.id.menu_save)
                menuItemSave.setVisible(true)
            },
            blockMenuItemSelected = {
                when (it.itemId) {
                    R.id.menu_alarm -> {
                        checkBeforeIvent { title ->
                            itemToDo.title = title
                            setReminder()
                        }
                    }
                    R.id.menu_notifications_off -> {
                        it.setVisible(false)
                        itemToDo.id?.let { uuid ->
                            deleteNotification(uuid, block = {
                                itemToDo.data = ""
                            }, blockTwo = {
                                viewModel.addToDo(itemToDo)
                            })
                        }

                    }
                    R.id.menu_save -> {
                        checkBeforeIvent { title ->
                            itemToDo.title = title
                            viewModel.addToDo(itemToDo)
                            hideKeyboard(requireActivity())
                            findNavController().navigateUp()
                        }
                        val items = arrayOfNulls<String>(allEds.size)
                        for (i in allEds.indices) {
                            items[i] = (allEds.get(i)
                                .findViewById<View>(R.id.editText) as EditText).text.toString()

                            Log.e(
                                "MainActivity",
                                (allEds.get(i)
                                    .findViewById<View>(R.id.editText) as EditText).text.toString()
                            )
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
                val month: String = SimpleDateFormat(
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

                val data = SimpleDateFormat(
                    DATE_TIME_FORMAT,
                    Locale.getDefault()
                )
                    .format(calendar.time)
                itemToDo.data = data
                setNotification(data)
                viewModel.addToDo(itemToDo)
                addEvent(itemToDo.title, calendar.time.time)
            }
        }
    }

    private fun setNotification(text: String) {
        val service = Intent(requireContext(), NotificationHelper::class.java)
        service.setAction("ACTION_START_FOREGROUND_SERVICE")
        service.putExtra("itemId", itemToDo.id)
        service.putExtra("itemTitle", itemToDo.title)
        service.putExtra("data", text)
        activity?.startForegroundService(service)
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
        (activity as MainNoteBookActivity).binding.selectContainerCard.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        (activity as MainNoteBookActivity).binding.selectContainerCard.visibility = View.GONE
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
                binding.textView2.setTextColor(resources.getColor(R.color.black, null))
                binding.llError.visibility = View.GONE
            }

            override fun afterTextChanged(sequence: Editable?) {}
        }
        binding.todoTitle.addTextChangedListener(textWatcher)
    }

    private fun isShowAnErrorInTheSelection(title: Boolean) {
        if (showAnErrorInTheSelection) {
            when (title) {
                true -> {
                    binding.llError.visibility = View.GONE
                    binding.textView2.setTextColor(resources.getColor(R.color.black, null))
                }
                false -> {
                    binding.llError.visibility = View.VISIBLE
                    binding.textView2.setTextColor(resources.getColor(R.color.color_red, null))
                    binding.textView2.shakeAndVibrate()
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


    private fun checkBeforeIvent(ivent: (String) -> Unit) {
        val title = binding.todoTitle.text.toString()
        if (verifyTitleFromUser(title)) {
            ivent.invoke(title)
        } else {
            showAnErrorInTheSelection = true
            isShowAnErrorInTheSelection(
                verifyTitleFromUser(title)
            )
        }
    }

    private fun setData(title: EditText) {
        title.setText(itemToDo.title)
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