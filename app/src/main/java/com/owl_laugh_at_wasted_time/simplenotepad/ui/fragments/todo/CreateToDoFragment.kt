package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.todo

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.domain.entity.SubTaskItem
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.getColorDrawable
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.shakeAndVibrate
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.FragmentCreateTodoBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.activity.MainNoteBookActivity
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.BaseFragment
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.viewBinding
import com.owl_laugh_at_wasted_time.viewmodel.todo.TodoListViewModel
import java.util.*


class CreateToDoFragment : BaseFragment(R.layout.fragment_create_todo) {

    private var itemToDo = ItemToDo()
    private var idItemToDo: UUID? = null
    private var showAnErrorInTheSelection = false
    private val binding by viewBinding(FragmentCreateTodoBinding::bind)
    private val viewModel by viewModels<TodoListViewModel> { viewModelFactory }
    private val args: CreateToDoFragmentArgs by navArgs()
    private var allEds: ArrayList<View> = arrayListOf()
    private lateinit var linear: LinearLayout
    private var setSubTasks = true


    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
        idItemToDo = args.todoId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linear = binding.linear
        binding.todoTitle.requestFocus()
        showKeyboard(binding.todoTitle)
        launchScope {
            if (idItemToDo != null) {
                viewModel.flowSubTask(idItemToDo!!).collectWhileStarted {
                    if (setSubTasks) {
                        for (i in it) {
                            addSubTaskLine(linear, i.text, i.done)
                        }
                    }
                    setSubTasks = false
                }
                itemToDo = viewModel.getNoteById(idItemToDo!!)
            }
            setData(binding.todoTitle)
        }
        setOnClickListeners()
        initMenu()

    }


    private fun addSubTaskLine(linear: LinearLayout, str: String = "", done: Boolean = false) {
        val viewSub: View = layoutInflater.inflate(R.layout.custom_edittext_layout, null)
        val deleteField: TextView = viewSub.findViewById(R.id.button2) as TextView
        deleteField.setOnClickListener {
            try {
                (viewSub.parent as LinearLayout).removeView(viewSub)
                allEds.remove(viewSub)
                viewModel.deleteItemById(str)
            } catch (ex: IndexOutOfBoundsException) {
                ex.printStackTrace()
            }
        }
        val text = viewSub.findViewById(R.id.editText) as EditText
        val chb = viewSub.findViewById(R.id.chb_current) as CheckBox
        chb.isChecked = done
        text.setText(str)
        text.requestFocus()
        allEds.add(viewSub)
        linear.addView(viewSub)
    }

    private fun setOnClickListeners() {
        binding.tvAdd.setOnClickListener {
            addSubTaskLine(linear)
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
        binding.indicatorColor.setOnClickListener {
            openPalette()
        }
    }

    private fun initMenu() {
        setToolBarMenu({
            val menuItemAlarm = it.findItem(R.id.menu_alarm)
            val menuItemSave = it.findItem(R.id.menu_save)
            menuItemAlarm.setVisible(true)
            menuItemSave.setVisible(true)
        }, {
            when (it.itemId) {
                R.id.menu_alarm -> {
                    checkBeforeIvent { title ->
                        itemToDo.title = title
                        setDateOfComplection(itemToDo) {
                            viewModel.addToDo(it)
                            addNotification(saveSubTaskList(), it)
                        }
                    }
                }
                R.id.menu_save -> {
                    checkBeforeIvent { title ->
                        itemToDo.title = title
                        viewModel.addToDo(itemToDo)
                        hideKeyboard(requireActivity())
                        findNavController().navigateUp()
                        addNotification(saveSubTaskList(), itemToDo)
                    }

                }
            }
        })
    }

    private fun saveSubTaskList(): ArrayList<String> {
        val array = arrayListOf<String>()
        var counter = 1
        for (i in allEds.indices) {
            val text = (allEds.get(i)
                .findViewById<View>(R.id.editText) as EditText).text.toString()
            val chb = allEds.get(i).findViewById(R.id.chb_current) as CheckBox
            if (text != "") {
                array.add("${counter++} - $text")
                viewModel.addSubTask(
                    SubTaskItem(
                        itemToDo.id!!,
                        text,
                        chb.isChecked
                    )
                )
            }
        }
        return array
    }

    override fun onStop() {
        super.onStop()
        (activity as MainNoteBookActivity).binding.selectContainerCard.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        (activity as MainNoteBookActivity).binding.selectContainerCard.visibility = View.GONE
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(sequence: Editable?) {}
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