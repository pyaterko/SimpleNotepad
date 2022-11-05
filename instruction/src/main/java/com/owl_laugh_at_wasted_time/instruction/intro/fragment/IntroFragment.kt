package com.owl_laugh_at_wasted_time.instruction.intro.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.owl_laugh_at_wasted_time.instruction.databinding.IntroSlideBinding

open class IntroFragment : Fragment() {
    lateinit var binding: IntroSlideBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = IntroSlideBinding.inflate(inflater, container, false)
        return binding.root
    }
}