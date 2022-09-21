package com.owl_laugh_at_wasted_time.simplenotepad.di

import com.owl_laugh_at_wasted_time.data.model.InShoppingListRepository
import com.owl_laugh_at_wasted_time.domain.repository.ShoppingListRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface RepositoryModule {


    @Singleton
    @Binds
    fun bindShoppingListRepository(impl: InShoppingListRepository): ShoppingListRepository



}