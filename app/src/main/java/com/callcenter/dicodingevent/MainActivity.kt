package com.callcenter.dicodingevent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_upcoming_events -> {
                    switchToEventFragment(1) // Upcoming events
                    true
                }
                R.id.action_past_events -> {
                    switchToEventFragment(0) // Past events
                    true
                }
                else -> false
            }
        }

        // Load the default fragment on startup
        if (savedInstanceState == null) {
            switchToEventFragment(1) // Default to upcoming events
        }
    }

    private fun switchToEventFragment(status: Int) {
        val fragment = EventListFragment.newInstance(status)
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}
