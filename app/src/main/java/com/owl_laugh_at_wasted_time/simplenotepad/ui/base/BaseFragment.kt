package com.owl_laugh_at_wasted_time.simplenotepad.ui.base

import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.launchAndRepeatOnStart
import com.owl_laugh_at_wasted_time.simplenotepad.ui.activity.MainActivity
import com.owl_laugh_at_wasted_time.viewmodel.base.ViewModelFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.sample
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


open class BaseFragment(layout: Int) : Fragment(layout) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    var isMain = true

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

    fun Flow<Throwable>.connectErrorData() {
        lifecycleScope.launch {
            sample(1000L).collect {
                if (lifecycle.currentState >= Lifecycle.State.STARTED) {
                    Toast.makeText(context, "default_error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}