package com.owl_laugh_at_wasted_time.simplenotepad.ui.base

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibe?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibe?.vibrate(100)
    }
//    vibe?.vibrate(100)
}


fun preferences(context: Context): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(context)

fun toDateString(value: String): String =
    try {
        SimpleDateFormat(DATE_FORMAT_IN, Locale.getDefault())
            .parse(value).let { date ->
                SimpleDateFormat(DATE_FORMAT_OUT, Locale.getDefault()).format(date!!)
            }
    } catch (err: Exception) {
        ""
    }

fun ItemColor.getColorDrawable(context: Context) =
    ContextCompat.getColor(context, getColorRes())

fun ItemColor.getColorRes() = when (this) {
    ItemColor.WHITE -> R.color.colorNotPriority
    ItemColor.YELLOW -> R.color.action_color
    ItemColor.GREEN -> R.color.color_green
    ItemColor.BLUE -> R.color.color_blue
    ItemColor.RED -> R.color.colorAccent
    ItemColor.VIOLET -> R.color.color_white
    ItemColor.PINK -> R.color.color_violet
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
        it.setBackgroundDrawableResource(R.drawable.background_selected)
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


