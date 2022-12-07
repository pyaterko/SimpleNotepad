package com.owl_laugh_at_wasted_time.settings.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.owl_laugh_at_wasted_time.settings.R

class SettingsInterface : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_interface, rootKey)
    }

}