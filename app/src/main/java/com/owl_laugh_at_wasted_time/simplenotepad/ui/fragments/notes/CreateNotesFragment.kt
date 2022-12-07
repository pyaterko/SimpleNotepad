package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.notes

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.owl_laugh_at_wasted_time.domain.UNDEFINED_ID
import com.owl_laugh_at_wasted_time.domain.entity.ItemCategory
import com.owl_laugh_at_wasted_time.domain.entity.ItemNote
import com.owl_laugh_at_wasted_time.domain.entity.ShoppingListItem
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.getColorDrawable
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.showActionAlertDialog
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.FragmentCreateNotesBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.activity.MainNoteBookActivity
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.BaseFragment
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.viewBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.adapters.OnClickCategory
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.adapters.createEditNotesCategoryAdapter
import com.owl_laugh_at_wasted_time.viewmodel.notes.CreateNoteViewModel
import com.owl_laugh_at_wasted_time.viewmodel.notes.NotesListViewModel

class CreateNotesFragment : BaseFragment(R.layout.fragment_create_notes), OnClickCategory {

    private var note = ItemNote()
    private val binding by viewBinding(FragmentCreateNotesBinding::bind)
    private val viewModel by viewModels<CreateNoteViewModel> { viewModelFactory }
    private val args: CreateNotesFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoryAdapter = createEditNotesCategoryAdapter(this)
        viewModel.categoriesLiveData.observe(viewLifecycleOwner){
            categoryAdapter.submitList(it)
        }
        binding.noteTitle.requestFocus()
        showKeyboard(binding.noteTitle)
        launchScope {
            val id = args.noteId
            if (id != UNDEFINED_ID) {
                note = viewModel.getNoteById(id)
            }
            setData(binding.noteTitle, binding.noteText)
        }
        binding.tvNoteCreateCategory.setOnClickListener {
            binding.llCategory.isVisible = true
        }
        binding.rvNoteCreateCategory.adapter = categoryAdapter
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
            binding.tvNoteCreateCategory.isVisible = true
        }

        binding.indicatorColor.setOnClickListener {
            openPalette()
            binding.tvNoteCreateCategory.isVisible = false
            binding.llCategory.isVisible = false
        }
        setToolBarMenu(
            blockCreateMenu = {
                val menuItemShare = it.findItem(R.id.menu_share)
                menuItemShare.setVisible(true)
                val menuItemSave = it.findItem(R.id.menu_save)
                menuItemSave.setVisible(true)
            },
            blockMenuItemSelected = {
                when (it.itemId) {
                    R.id.menu_share -> {
                        Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "${binding.noteTitle.text}\n${binding.noteText.text}"
                            )
                        }.also { intent ->
                            val chooserIntent =
                                Intent.createChooser(
                                    intent,
                                    getString(R.string.share_note)
                                )
                            startActivity(chooserIntent)
                        }
                    }
                    R.id.menu_save -> {
                        note.title = binding.noteTitle.text.toString()
                        note.text = binding.noteText.text.toString()
                        launchScope {
                            viewModel.addItemNote(note)
                        }
                        hideKeyboard(requireActivity())
                        findNavController().navigateUp()
                    }
                }
            })

        binding.tvAddCategory.setOnClickListener {
            showActionAlertDialog(
                requireContext(),
                layoutInflater,
                getString(R.string.add_category),
                getString(R.string.empty_value),
                R.string.action_confirm,
                R.string.name_of,
                actionPB1 = {
                    if (it.isNotBlank()) {
                        viewModel.addCategory(it)
                    }
                }
            )
        }
    }

    override fun onStart() {
        super.onStart()
        (activity as MainNoteBookActivity).binding.selectContainerCard.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        (activity as MainNoteBookActivity).binding.selectContainerCard.visibility = View.VISIBLE
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

    override fun onClickCategoryItem(item: ItemCategory) {
        if (item.name=="Все"){
            binding.tvNoteCreateCategory.text = "без категории"
        }else{
            binding.tvNoteCreateCategory.text = item.name
        }
        binding.llCategory.isVisible = false
        note.category = item.name
    }

    override fun deleteCategory(item: ItemCategory) {
       viewModel.deleteCategory(item.id)
    }

}