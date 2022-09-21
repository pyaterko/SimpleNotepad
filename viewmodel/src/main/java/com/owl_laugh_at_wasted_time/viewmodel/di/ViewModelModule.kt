package com.owl_laugh_at_wasted_time.viewmodel.di

import androidx.lifecycle.ViewModel
import com.owl_laugh_at_wasted_time.viewmodel.shopping.ShoppingListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {


    @Binds
    @IntoMap
    @ViewModelKey(ShoppingListViewModel::class)
    fun bindShoppingListViewModel(viewModel: ShoppingListViewModel): ViewModel


}