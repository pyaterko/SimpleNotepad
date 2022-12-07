package com.owl_laugh_at_wasted_time.settings.fragments


import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

import com.owl_laugh_at_wasted_time.instruction.activity.InstructionActivity
import com.owl_laugh_at_wasted_time.settings.R


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        initPreference()
    }

    private fun initPreference() {
        val data = findPreference<Preference>(getString(R.string.settings_data))
        val interfaces = findPreference<Preference>(getString(R.string.settings_interface))
        val tourShow = findPreference<Preference>(getString(R.string.settings_tour_show))
        val behaviors = findPreference<Preference>(getString(R.string.settings_behaviors))
        val version = findPreference<Preference>(getString(R.string.settings_changel))

        version?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_chengelogFragment)
            return@setOnPreferenceClickListener true
        }

        data?.setOnPreferenceClickListener {
         findNavController().navigate(R.id.action_settingsFragment_to_settingsData)
            return@setOnPreferenceClickListener true
        }
        interfaces?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_settingsInterfase)
            return@setOnPreferenceClickListener true
        }
        behaviors?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_settingsBehavuors)
            return@setOnPreferenceClickListener true
        }

        tourShow?.setOnPreferenceClickListener {
            startActivity(Intent(requireContext(), InstructionActivity::class.java))
            return@setOnPreferenceClickListener true
        }

    }

}
