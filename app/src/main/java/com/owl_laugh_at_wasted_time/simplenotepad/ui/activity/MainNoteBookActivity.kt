package com.owl_laugh_at_wasted_time.simplenotepad.ui.activity


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.tabs.TabLayout
import com.owl_laugh_at_wasted_time.instruction.intro.activity.IntroActivity
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.preferences
import com.owl_laugh_at_wasted_time.simplenotepad.Initializer
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.ActivityMainBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.viewBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.todo.ToDoListFragmentDirections


class MainNoteBookActivity : AppCompatActivity() {

    val binding by viewBinding(ActivityMainBinding::inflate)
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    val component by lazy {
        Initializer.component(this)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_name"
            val descriptionText = "channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID", name, importance)
                .apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        createNotificationChannel()

        if (preferences(this).getBoolean(CURRENT_BOOLEAN_STATE, true)) {
            startActivity(Intent(applicationContext, IntroActivity::class.java))
            preferences(this).edit().putBoolean(CURRENT_BOOLEAN_STATE, false).apply()
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.toDoListFragment,
            R.id.createToDoFragment,
            R.id.createNotesFragment,
            R.id.readFragment
        ).build()
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        binding.selectTabs.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        navController.navigateUp()


                    }
                    1 -> {
                        navController.navigateUp()
                        navController.navigate(ToDoListFragmentDirections.actionToDoListFragmentToNotesListFragment())
                    }
                    2 -> {
                        navController.navigateUp()
                        navController.navigate(ToDoListFragmentDirections.actionToDoListFragmentToShoppingListFragment())
                    }
                }
            }
        })

    }

    override fun onSupportNavigateUp(): Boolean {
        selectHome()
        return super.onSupportNavigateUp()
    }

    private fun selectHome() {
        navController.navigateUp()
        val tab: TabLayout.Tab? = binding.selectTabs.getTabAt(0)
        tab?.select()
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
        private const val CURRENT_BOOLEAN_STATE = "CURRENT_BOOLEAN_STATE"
        private const val REQUEST_WRITE_EXTERNAL_PERMISSION = 2
    }
}