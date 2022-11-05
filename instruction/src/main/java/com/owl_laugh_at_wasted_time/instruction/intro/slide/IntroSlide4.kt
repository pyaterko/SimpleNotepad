package com.owl_laugh_at_wasted_time.instruction.intro.slide

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.owl_laugh_at_wasted_time.instruction.R
import com.owl_laugh_at_wasted_time.instruction.intro.fragment.IntroFragment

class IntroSlide4 : IntroFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.introBackground.setBackgroundColor(Color.parseColor("#60FDD835"))
        binding.introTitle.setText(R.string.tour_detailactivity_attachment_title)
        binding.introImage.setImageResource(R.drawable.slide4)
        binding.introDescription.setText(R.string.tour_detailactivity_attachment_detail)
    }
}

