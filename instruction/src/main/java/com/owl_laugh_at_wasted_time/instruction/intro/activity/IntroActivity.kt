package com.owl_laugh_at_wasted_time.instruction.intro.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2
import com.owl_laugh_at_wasted_time.instruction.intro.slide.*

class IntroActivity : AppIntro2() {

    override fun init(savedInstanceState: Bundle?) {
        addSlide(IntroSlide1() as Fragment, applicationContext)
        addSlide(IntroSlide2() as Fragment, applicationContext)
        addSlide(IntroSlide3() as Fragment, applicationContext)
        addSlide(IntroSlide4() as Fragment, applicationContext)
        addSlide(IntroSlide5() as Fragment, applicationContext)
        addSlide(IntroSlide6() as Fragment, applicationContext)
    }

    override fun onDonePressed() {
        finish()
    }

    override fun onBackPressed() {

    }

}
