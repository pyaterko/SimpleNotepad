package com.owl_laugh_at_wasted_time.instruction.intro.slide

import android.os.Bundle
import android.view.View
import com.owl_laugh_at_wasted_time.instruction.R
import com.owl_laugh_at_wasted_time.instruction.intro.fragment.IntroFragment

class IntroSlide6 : IntroFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.introTitle.setText(R.string.tour_listactivity_final_title)
        binding.introImage.setImageResource(R.drawable.slide6)
        binding.introDescription.setText(R.string.tour_community)
    }
}

