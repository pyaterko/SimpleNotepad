package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.read

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.FragmentReadBinding


class ReadFragment : Fragment(R.layout.fragment_read) {

    private lateinit var binding: FragmentReadBinding
    private lateinit var text: String
    private lateinit var title: String
    private var category = true
    private var counter = 0f
    private var size: Float = 0f


    override fun onAttach(context: Context) {
        super.onAttach(context)
        text = requireArguments().getString(READ_KEY_TEXT).toString()
        title = requireArguments().getString(READ_KEY_TITLE).toString()
        category=requireArguments().getBoolean(CATEGORY)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentReadBinding.bind(view)
        binding.textRead.setText("$title $text")

        counter = pixelsToSp(binding.textRead.textSize)

        binding.readLayout.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeLeft() = changeText(false)
            override fun onSwipeRight() = changeText(true)
        })

        binding.textRead.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeLeft() = changeText(false)
            override fun onSwipeRight() = changeText(true)
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

    companion object {
        const val READ_KEY_TEXT = "text"
        const val CATEGORY="CATEGORY"
        const val READ_KEY_TITLE = "READ_KEY_TITLE"

    }
}