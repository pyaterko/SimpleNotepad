package com.owl_laugh_at_wasted_time.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.owl_laugh_at_wasted_time.settings.R
import com.owl_laugh_at_wasted_time.settings.databinding.FragmentChengelogBinding

class ChangelogFragment : Fragment(R.layout.fragment_chengelog) {

    private var _binding: FragmentChengelogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChengelogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}