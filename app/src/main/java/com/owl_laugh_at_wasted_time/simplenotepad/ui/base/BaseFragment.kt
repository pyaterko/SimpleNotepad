package com.owl_laugh_at_wasted_time.simplenotepad.ui.base

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.launchAndRepeatOnStart
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.preferences
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.ui.activity.MainActivity
import com.owl_laugh_at_wasted_time.viewmodel.base.ViewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


open class BaseFragment(layout: Int) : Fragment(layout) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    val component by lazy {
        (activity as MainActivity).component
    }

    fun launchScope(block: suspend () -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                block.invoke()
            }
        }
    }

    fun <T> Flow<T>.collectWhileStarted(block: (T) -> Unit) {
        launchAndRepeatOnStart {
            collect { block.invoke(it) }
        }
    }

    fun setupSwipe(recyclerView: RecyclerView?, block: (RecyclerView.ViewHolder) -> Unit) {
        val callBack = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                block.invoke(viewHolder)
            }
        }
        val swipe = preferences(requireContext()).getBoolean(
            getString(R.string.settings_swipe_to_trash_key),
            false
        )
        if (swipe) {
            val itemTouchHelper = ItemTouchHelper(callBack)
            itemTouchHelper.attachToRecyclerView(recyclerView)
        }
    }

    fun setToolBarMenu(
        blockCreateMenu: ((Menu) -> Unit)?,
        blockMenuItemSelected: ((MenuItem) -> Unit)?
    ) {
        val menuHost: MenuHost = activity as MenuHost
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)
                blockCreateMenu?.invoke(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                blockMenuItemSelected?.invoke(menuItem)
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    fun launchFragment(directions: NavDirections) {
        findNavController().navigate(
            directions,
            navOptions {
                anim {
                    enter = R.anim.enter
                    exit = R.anim.exit
                    popEnter = R.anim.pop_enter
                    popExit = R.anim.pop_exit
                }
            }
        )
    }

}