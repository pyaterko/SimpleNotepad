package com.owl_laugh_at_wasted_time.simplenotepad

import android.content.Context
import com.owl_laugh_at_wasted_time.simplenotepad.di.AppComponent
import com.owl_laugh_at_wasted_time.simplenotepad.di.DaggerAppComponent


object Initializer {
    fun component(context: Context): AppComponent {
        return DaggerAppComponent.factory().create(context)
    }
}