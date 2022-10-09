package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.todo

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.owl_laugh_at_wasted_time.domain.UNDEFINED_ID
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.*
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.FragmentCreateTodoBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.BaseFragment
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.viewBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.notes.CreateNotesFragment
import com.owl_laugh_at_wasted_time.viewmodel.todo.TodoListViewModel


class CreateToDoFragment : BaseFragment(R.layout.fragment_create_todo) {

    private var itemToDo = ItemToDo()
    private var showAnErrorInTheSelection = false
    private val binding by viewBinding(FragmentCreateTodoBinding::bind)
    private val viewModel by viewModels<TodoListViewModel> { viewModelFactory }
    private val args:CreateToDoFragmentArgs by navArgs()

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
        setFabOnClickListener()
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
    }

    override fun onStart() {
        super.onStart()
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
        return !(priorety.equals("Выберите приоритет"))
    }

    private fun showError() {
        if ((binding.todoTitle.text.toString().isNotEmpty()) &&
            !(binding.currentPrioritiesSpinner.selectedItem.toString()).equals("Выберите приоритет")
        ) {
            binding.llError.visibility = View.GONE
        }
    }

    private fun setFabOnClickListener() {
        binding.fabCreateNote.setOnClickListener {
            val title = binding.todoTitle.text.toString()
            val description = binding.todoText.text.toString()
            val getPriority = binding.currentPrioritiesSpinner.selectedItem.toString()
            if (verifyTitleFromUser(title) &&
                verifyPrioretyFromUser(getPriority)
            ) {
                itemToDo.title = title
                itemToDo.text = description
                itemToDo.priority = parsePriority(getPriority)
                launchScope {
                    viewModel.addItemNote(itemToDo)
                }
                findNavController().navigateUp()
            } else {
                showAnErrorInTheSelection = true
                isShowAnErrorInTheSelection(
                    verifyTitleFromUser(title),
                    verifyPrioretyFromUser(getPriority)
                )
            }
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