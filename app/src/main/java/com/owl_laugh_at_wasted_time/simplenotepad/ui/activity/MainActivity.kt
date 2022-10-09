package com.owl_laugh_at_wasted_time.simplenotepad.ui.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.tabs.TabLayout
import com.owl_laugh_at_wasted_time.simplenotepad.Initializer
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.ActivityMainBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.viewBinding

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private lateinit var navController: NavController

    val component by lazy {
        Initializer.component(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.selectTabs.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        launchFragment(R.id.toDoListFragment)
                    }
                    1 -> {
                        launchFragment(R.id.notesListFragment)
                    }
                    2 -> {
                        launchFragment(R.id.shoppingListFragment)
                    }
                }
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_WRITE_EXTERNAL_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    applicationContext,
                    "разрешение получено", Toast.LENGTH_SHORT
                ).show()

            } else {
                Toast.makeText(
                    applicationContext,
                    "Пользователь отклонил запрос", Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun launchFragment(destination: Int) {
        navController.popBackStack()
        navController.popBackStack()
        navController.navigate(destination)
    }

    companion object {
        private const val REQUEST_WRITE_EXTERNAL_PERMISSION = 2
    }
}