package com.owl_laugh_at_wasted_time.instruction.intro.slide

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.owl_laugh_at_wasted_time.instruction.R
import com.owl_laugh_at_wasted_time.instruction.intro.fragment.IntroFragment

class IntroSlide2 : IntroFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.introBackground.setBackgroundColor(Color.parseColor("#71F44336"))
        binding.introTitle.setText(R.string.tour_listactivity_home_title)
        binding.introImage.setImageResource(R.drawable.slide2)
        binding.introDescription.setText(R.string.tour_listactivity_home_detail)
    }
}

