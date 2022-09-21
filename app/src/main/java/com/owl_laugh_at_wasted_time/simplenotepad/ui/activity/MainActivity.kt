package com.owl_laugh_at_wasted_time.simplenotepad.ui.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.owl_laugh_at_wasted_time.simplenotepad.Initializer
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.ActivityMainBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.ScreenNavigation
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.viewBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.notes.NotesListFragment
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.shopping.ShoppingListFragment

class MainActivity : AppCompatActivity(R.layout.activity_main),ScreenNavigation {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    val component by lazy {
        Initializer.component(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       setContentView(binding.root)
        binding.selectTabs.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        Toast.makeText(this@MainActivity, "0", Toast.LENGTH_LONG).show()
                    }
                    1 -> {
                        launchFragment(NotesListFragment.newInstance())
                    }
                    2 -> {
                       launchFragment(ShoppingListFragment.newInstance())
                    }

                }
            }
        })
    }


    override fun exit() {
      finish()
    }

    override fun launchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .setCustomAnimations(
                R.anim.enter,
                R.anim.exit,
                R.anim.pop_enter,
                R.anim.pop_exit
            )
            .commit()
    }

    override fun launchFragmentaddToBackStack(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .setCustomAnimations(
                R.anim.enter,
                R.anim.exit,
                R.anim.pop_enter,
                R.anim.pop_exit
            )
            .addToBackStack(null)
            .commit()
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

    companion object {
        private const val REQUEST_WRITE_EXTERNAL_PERMISSION = 2
    }
}