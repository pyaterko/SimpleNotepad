package com.owl_laugh_at_wasted_time.instruction.intro.slide

import android.os.Bundle
import android.view.View
import com.owl_laugh_at_wasted_time.instruction.R
import com.owl_laugh_at_wasted_time.instruction.intro.fragment.IntroFragment

class IntroSlide1 : IntroFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //   binding.introBackground.setBackgroundColor(Color.parseColor("#222222"))
        binding.introTitle.setText(R.string.app_name)
        binding.introImage.setVisibility(View.GONE)
        binding.introImageSmall.setImageResource(R.drawable.stock_photo)
        binding.introImageSmall.setVisibility(View.VISIBLE)
        binding.introDescription.setText(R.string.tour_listactivity_final_detail)
    }
}

