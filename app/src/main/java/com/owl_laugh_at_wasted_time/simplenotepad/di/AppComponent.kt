package com.owl_laugh_at_wasted_time.simplenotepad.di

import android.content.Context
import com.owl_laugh_at_wasted_time.data.di.DataModule
import com.owl_laugh_at_wasted_time.simplenotepad.ui.activity.MainActivity
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.shopping.ShoppingListFragment
import com.owl_laugh_at_wasted_time.viewmodel.di.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules =
    [
        DataModule::class,
        ViewModelModule::class,
        RepositoryModule::class
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
    fun inject(fragment: ShoppingListFragment)

}