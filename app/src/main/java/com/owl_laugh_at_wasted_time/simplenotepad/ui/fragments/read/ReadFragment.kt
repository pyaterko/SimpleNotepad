package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.read

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.FragmentReadBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.activity.MainNoteBookActivity
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.BaseFragment


class ReadFragment : BaseFragment(R.layout.fragment_read) {

    private lateinit var binding: FragmentReadBinding
    private lateinit var text: String
    private lateinit var title: String
    private var category = true
    private var counter = 0f
    private var size: Float = 0f
    private val args: ReadFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        text = args.text
        title = args.title
        category = args.category
    }

    override fun onStart() {
        super.onStart()
        (activity as MainNoteBookActivity).binding.selectContainerCard.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        (activity as MainNoteBookActivity).binding.selectContainerCard.visibility = View.VISIBLE
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentReadBinding.bind(view)
        binding.textRead.text = text

        counter = pixelsToSp(binding.textRead.textSize)

        binding.readLayout.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeLeft() = changeText(false)
            override fun onSwipeRight() = changeText(true)
        })

        binding.textRead.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeLeft() = changeText(false)
            override fun onSwipeRight() = changeText(true)
        })
        setToolBarMenu(
            blockCreateMenu = {
                val menuItemShare = it.findItem(R.id.menu_share)
                menuItemShare.isVisible = true
            },
            blockMenuItemSelected = {
                when (it.itemId) {
                    R.id.menu_share -> {
                        Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                text
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
                }
            })
    }

    private fun changeText(increment: Boolean) {
        size = if (increment) {
            counter = counter.plus(5F)
            counter
        } else {
            counter = counter.minus(5F)
            counter
        }
        binding.textRead.textSize = size
    }

    private fun pixelsToSp(px: Float): Float {
        val scaledDensity = resources.displayMetrics.scaledDensity
        return px / scaledDensity
    }


}