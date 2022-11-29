package com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Vibrator
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import com.owl_laugh_at_wasted_time.domain.DATE_FORMAT_IN
import com.owl_laugh_at_wasted_time.domain.DATE_FORMAT_OUT
import com.owl_laugh_at_wasted_time.domain.entity.ItemColor
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.DialogShoppingListBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

fun Fragment.launchAndRepeatOnStart(block: suspend () -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            block.invoke()
        }
    }
}

fun Context.dip(value: Int) = resources.displayMetrics.density * value

fun View.dip(value: Int) = context.dip(value)

fun View.shakeAndVibrate() {
    val vibe = ContextCompat.getSystemService(context, Vibrator::class.java)
    val shake = TranslateAnimation(0F, 10F, 0F, 0F)
    shake.duration = 550
    shake.interpolator = CycleInterpolator(5F)
    startAnimation(shake)
    vibe?.vibrate(100)
}

fun preferences(context: Context) =
    PreferenceManager.getDefaultSharedPreferences(context)

fun toDateString(value: String): String =
    try {
        SimpleDateFormat(DATE_FORMAT_IN, Locale.getDefault())
            .parse(value).let { date ->
                SimpleDateFormat(DATE_FORMAT_OUT, Locale.getDefault()).format(date)
            }
    } catch (err: Exception) {
        ""
    }

fun ItemColor.getColorDrawable(context: Context) =
    ContextCompat.getColor(context, getColorRes())

fun ItemColor.getColorRes() = when (this) {
    ItemColor.WHITE -> R.color.purple_500
    ItemColor.VIOLET -> R.color.color_text_secondary
    ItemColor.YELLOW -> R.color.color_yellow
    ItemColor.RED -> R.color.color_red
    ItemColor.PINK -> R.color.color_pink
    ItemColor.GREEN -> R.color.color_green
    ItemColor.BLUE -> R.color.color_blue
}

fun ItemColor.getColorString() = when (this) {
    ItemColor.WHITE -> "WHITE"
    ItemColor.VIOLET -> "VIOLET"
    ItemColor.YELLOW -> "YELLOW"
    ItemColor.RED -> "RED"
    ItemColor.PINK -> "PINK"
    ItemColor.GREEN -> "GREEN"
    ItemColor.BLUE -> "BLUE"
}

fun listener(context: Context, lyambda: () -> Unit): AdapterView.OnItemSelectedListener = object :
    AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(p0: AdapterView<*>?) {}
    override fun onItemSelected(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        when (position) {
            0 -> {
                (parent?.getChildAt(0) as TextView).setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.black
                    )
                )
            }
            1 -> {
                lyambda.invoke()
                (parent?.getChildAt(0) as TextView).setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_red
                    )
                )
            }
            2 -> {
                lyambda.invoke()
                (parent?.getChildAt(0) as TextView).setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_green
                    )
                )
            }
            3 -> {
                lyambda.invoke()
                (parent?.getChildAt(0) as TextView).setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorNotPriority
                    )
                )
            }
        }
    }
}

fun displayAConfirmationDialog(
    context: Context,
    title: String,
    actionPB1: (() -> Unit)? = null,
    actionNB1: (() -> Unit)? = null,
) {
    val listener = DialogInterface.OnClickListener { _, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                actionPB1?.invoke()
            }
            DialogInterface.BUTTON_NEGATIVE -> {}
            DialogInterface.BUTTON_NEUTRAL -> {
                actionNB1?.invoke()
            }
        }
    }
    val titleView = TextView(context)
    titleView.text = title
    titleView.setPadding(10, 10, 10, 10)
    titleView.gravity = Gravity.CENTER
    titleView.setTextColor(Color.WHITE)
    titleView.textSize = 20f
    val dialog = android.app.AlertDialog.Builder(context)
        .setCancelable(true)
        .setCustomTitle(titleView)
        .setPositiveButton(R.string.action_yes, listener)
        .setNeutralButton(R.string.action_no, listener)
        .create()
    dialog.setCanceledOnTouchOutside(false)
    dialog.show()

    dialog?.window?.let {
        val lp = it.attributes
        it.setGravity(Gravity.BOTTOM)
        lp.y = 200
        it.setBackgroundDrawableResource(R.drawable.backgraund_selected)
    }
}

fun showActionAlertDialog(
    context: Context,
    layoutInflater: LayoutInflater,
    title: String,
    error: String,
    button: Int,
    hint: Int,
    actionPB1: ((name: String) -> Unit)? = null,
) {
    val dialogBinding = DialogShoppingListBinding.inflate(layoutInflater)
    dialogBinding.dialogShoppingListInputEditText.setHint(hint)
    val dialog = AlertDialog.Builder(context)
        .setTitle(title)
        .setView(dialogBinding.root)
        .setPositiveButton(button, null)
        .create()
    dialog.setOnShowListener {
        dialogBinding.dialogShoppingListInputEditText.requestFocus()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val enteredText = dialogBinding.dialogShoppingListInputEditText.text.toString()
            if (enteredText.isBlank()) {
                dialogBinding.dialogShoppingListInputEditText.error = error
                return@setOnClickListener
            }
            actionPB1?.invoke(enteredText)
            dialog.dismiss()
        }
    }
    dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    dialog.show()
    dialog.window?.let {
        val lp = it.attributes
        it.setGravity(Gravity.BOTTOM)
        lp.y = 200
        it.setBackgroundDrawableResource(R.drawable.btn_bg)
    }
    dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

}


