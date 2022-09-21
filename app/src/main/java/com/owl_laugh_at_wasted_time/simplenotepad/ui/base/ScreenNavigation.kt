package com.owl_laugh_at_wasted_time.simplenotepad.ui.base

import androidx.fragment.app.Fragment

interface ScreenNavigation {
    fun exit()
    fun launchFragment(fragment: Fragment)
    fun launchFragmentaddToBackStack(fragment: Fragment)
}