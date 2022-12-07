package com.owl_laugh_at_wasted_time.simplenotepad.ui.base.actions

import android.content.Context
import com.owl_laugh_at_wasted_time.domain.repository.UiActions
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.preferences
import javax.inject.Inject

class AndroidUiActions @Inject constructor(
    private val application: Context
) : UiActions {

    override fun notPopulateInitialData() {
        preferences(application).edit().putBoolean("POPULATE_INITIAL_DATA", false).apply()
    }

    override fun isPopulateInitialData() =
        preferences(application).getBoolean("POPULATE_INITIAL_DATA", true)

}