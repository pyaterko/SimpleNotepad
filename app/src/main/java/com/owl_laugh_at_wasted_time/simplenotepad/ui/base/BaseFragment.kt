package com.owl_laugh_at_wasted_time.simplenotepad.ui.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
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
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.owl_laugh_at_wasted_time.domain.DATE_TIME_FORMAT
import com.owl_laugh_at_wasted_time.domain.TIME_STRING_DAY
import com.owl_laugh_at_wasted_time.domain.TIME_STRING_MONTH
import com.owl_laugh_at_wasted_time.domain.TIME_STRING_YEAR
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.launchAndRepeatOnStart
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.preferences
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.ui.activity.MainNoteBookActivity
import com.owl_laugh_at_wasted_time.simplenotepad.ui.notification.NotificationHelper
import com.owl_laugh_at_wasted_time.viewmodel.base.ViewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


open class BaseFragment(layout: Int) : Fragment(layout) {

    @Inject
    protected lateinit var viewModelFactory: ViewModelFactory

    protected val component by lazy {
        (activity as MainNoteBookActivity).component
    }

    protected fun launchScope(block: suspend () -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                block.invoke()
            }
        }
    }

    protected fun <T> Flow<T>.collectWhileStarted(block: (T) -> Unit) {
        launchAndRepeatOnStart {
            collect { block.invoke(it) }
        }
    }

    protected fun setToolBarMenu(
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

    protected fun launchFragment(directions: NavDirections) {
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

    protected fun deleteNotification(itemId: Int, blockTwo: () -> Unit) {
        val service = Intent(requireContext(), NotificationHelper::class.java)
        service.setAction("ACTION_STOP_FOREGROUND_SERVICE")
        service.putExtra("itemId", itemId.hashCode())
        activity?.startService(service)
        blockTwo.invoke()
    }

    protected fun setDateOfComplection(itemToDo: ItemToDo, add: (ItemToDo) -> Unit) {
        val dateRangePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Выберите дату для напоминания")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        dateRangePicker.show(parentFragmentManager, "TAGTAG")
        dateRangePicker.addOnPositiveButtonClickListener {
            val materialTimePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(0)
                .setMinute(0)
                .setTitleText("Выберите время для напоминания")
                .build()
            materialTimePicker.show(parentFragmentManager, "TAG")
            materialTimePicker.addOnPositiveButtonClickListener {
                val month: String = SimpleDateFormat(
                    TIME_STRING_MONTH,
                    Locale.getDefault()
                ).format(Date(dateRangePicker.selection!!))
                val year: String = SimpleDateFormat(
                    TIME_STRING_YEAR,
                    Locale.getDefault()
                ).format(Date(dateRangePicker.selection!!))
                val day: String = SimpleDateFormat(
                    TIME_STRING_DAY,
                    Locale.getDefault()
                ).format(Date(dateRangePicker.selection!!))
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_MONTH, day.toInt())
                calendar.set(Calendar.YEAR, year.toInt())
                calendar.set(Calendar.MONTH, month.toInt() - 1)
                calendar.set(Calendar.HOUR_OF_DAY, materialTimePicker.hour)
                calendar.set(Calendar.MINUTE, materialTimePicker.minute)

                val data = SimpleDateFormat(
                    DATE_TIME_FORMAT,
                    Locale.getDefault()
                )
                    .format(calendar.time)
                itemToDo.data = data
                add.invoke(itemToDo)
                addEvent(itemToDo.title, calendar.time.time)
            }
        }
    }

    protected fun setNotification(arrayList: ArrayList<String>, itemToDo: ItemToDo) {
        val service = Intent(requireContext(), NotificationHelper::class.java)
        service.setAction("ACTION_START_FOREGROUND_SERVICE")
        service.putExtra("itemId", itemToDo.id.hashCode())
        service.putExtra("itemTitle", itemToDo.title)
        service.putStringArrayListExtra("array", arrayList)
        service.putExtra("data", itemToDo.dateOfCreation)
        activity?.startService(service)
    }

    private fun addEvent(title: String, begin: Long) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
        }
        startActivity(intent)
    }

    protected fun hideKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusedView = activity.currentFocus
        currentFocusedView?.let {
            inputMethodManager.hideSoftInputFromWindow(
                currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    protected fun showKeyboard(view: View) {
        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
        imm!!.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    protected fun fabActionOnScroll(
        recyclerView: RecyclerView,
        fab: FloatingActionButton?,
        show: (() -> Unit)?,
        hide: (() -> Unit)?
    ) {
        recyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        fab?.visibility = View.INVISIBLE
                        hide?.invoke()
                    } else if (dy < 0) {
                        show?.invoke()
                        fab?.visibility = View.VISIBLE
                    } else {
                        Log.d("TAG", "No Vertical Scrolled")
                    }
                }
            }
        )
    }
}