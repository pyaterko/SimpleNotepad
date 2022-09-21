package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.notes

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import com.owl_laugh_at_wasted_time.domain.entity.ItemNote
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.getColorDrawable
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.navigator
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.FragmentCreateNotesBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.BaseFragment
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.viewBinding
import com.owl_laugh_at_wasted_time.viewmodel.notes.NotesListViewModel


class CreateNotesFragment : BaseFragment(R.layout.fragment_create_notes) {


    private var note = ItemNote()
    private val binding by viewBinding(FragmentCreateNotesBinding::bind)
    private val viewModel by viewModels<NotesListViewModel> { viewModelFactory }

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
                    navigator().launchFragment(NotesListFragment.newInstance())
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchScope {
            val id = requireArguments().getInt(FILE_NAME)
            if (id != UNDEFINED_ID) {
                note = viewModel.getNoteById(id)
            }
            setData(binding.noteTitle, binding.noteText)
        }
        setFabOnClickListener()
        binding.colorPicturesNote.onColorClickListener = {
            note.color = it
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


    private fun setFabOnClickListener() {
        binding.fabCreateNote.setOnClickListener {
            note.title = binding.noteTitle.text.toString()
            note.text = binding.noteText.text.toString()
            launchScope {
                viewModel.addItemNote(note)
            }
            navigator().launchFragment(NotesListFragment.newInstance())
        }
    }

    private fun closePalette() {
        if (binding.colorPicturesNote.isOpen) {
            binding.colorPicturesNote.close()
        }
    }

    private fun openPalette() {
        if (!(binding.colorPicturesNote.isOpen)) {
            binding.colorPicturesNote.open()
        }
    }

    private fun setData(title: EditText, text: EditText) {
        title.setText(note.title)
        text.setText(note.text)
        setColorInIndicator(note.color.getColorDrawable(requireContext()))
    }

    private fun setColorInIndicator(color: Int) {
        binding.indicatorColor.setBackgroundTintList(ColorStateList.valueOf(color))
    }


    companion object {
        const val UNDEFINED_ID = 0
        private const val FILE_NAME = "FILE_NAME"
        fun newInstance(name: Int = UNDEFINED_ID): CreateNotesFragment {
            return CreateNotesFragment().apply {
                arguments = Bundle().apply {
                    putInt(FILE_NAME, name)
                }
            }
        }
    }
}