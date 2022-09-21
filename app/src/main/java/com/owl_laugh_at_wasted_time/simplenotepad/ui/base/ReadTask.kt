package com.owl_laugh_at_wasted_time.simplenotepad.ui.base

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.net.Uri
import android.os.AsyncTask
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.ui.activity.MainActivity
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.ref.WeakReference

@SuppressLint("StaticFieldLeak")
class ReadTask(editor: MainActivity, private val block: (str: String) -> Unit) :
    AsyncTask<Uri?, Void?, CharSequence>() {
    private val editorWeakReference: WeakReference<MainActivity>
    val s = "UTF-8"
    private var match = s

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(result: CharSequence) {
        val textFromFile = result.toString()
        block.invoke(textFromFile)
    }

    init {
        editorWeakReference = WeakReference<MainActivity>(editor)
    }

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg uris: Uri?): CharSequence {
        val stringBuilder = StringBuilder()
        val editor =
            editorWeakReference.get() ?: return stringBuilder

        try {
            BufferedInputStream(
                editor.getContentResolver().openInputStream(uris[0]!!)
            ).use { `in` ->

                var reader: BufferedReader? = null
                reader =
                    BufferedReader(InputStreamReader(`in`, match))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                    stringBuilder.append(System.getProperty("line.separator"))
                }
            }
        } catch (e: java.lang.Exception) {
            editor.runOnUiThread {
                alertDialog(
                    R.string.app_name,
                    e.message!!,
                    R.string.action_yes,
                    editor
                )
            }
            e.printStackTrace()
        }
        return stringBuilder
    }

    private fun alertDialog(
        title: Int,
        message: String,
        neutralButton: Int,
        editor: MainActivity
    ) {
        val builder = AlertDialog.Builder(editor)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setNeutralButton(neutralButton, null)
        builder.show()
    }
}