package com.owl_laugh_at_wasted_time.simplenotepad.di

import android.content.Context
import com.owl_laugh_at_wasted_time.data.di.DataModule
import com.owl_laugh_at_wasted_time.simplenotepad.ui.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules =
    [
        DataModule::class
    ]

)
interface AppComponent {

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance applicationContext: Context
        ): AppComponent
    }

    fun context(): Context
    fun inject(activity: MainActivity)

}