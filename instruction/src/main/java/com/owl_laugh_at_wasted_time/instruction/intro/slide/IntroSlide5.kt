package com.owl_laugh_at_wasted_time.instruction.intro.slide

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.owl_laugh_at_wasted_time.instruction.R
import com.owl_laugh_at_wasted_time.instruction.intro.fragment.IntroFragment

class IntroSlide5 : IntroFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.introBackground.setBackgroundColor(Color.parseColor("#41BB86FC"))
        binding.introTitle.setText(R.string.shopping_list_lable)
        binding.introImage.setImageResource(R.drawable.slide5)
        binding.introDescription.setText(R.string.tour_detailactivity_links_detail)
    }
}

