package com.owl_laugh_at_wasted_time.instruction.intro.slide

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.owl_laugh_at_wasted_time.instruction.R
import com.owl_laugh_at_wasted_time.instruction.intro.fragment.IntroFragment

class IntroSlide3 : IntroFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.introBackground.setBackgroundColor(Color.parseColor("#8bc34a"))
        binding.introTitle.setText(R.string.categories)
        binding.introImage.setImageResource(R.drawable.slide3)
        binding.introDescription.setText(R.string.tour_listactivity_tag_detail)
    }
}

