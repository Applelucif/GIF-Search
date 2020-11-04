package com.example.gyphyclient.view.ui

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.example.gyphyclient.R
import com.example.gyphyclient.internal.setupWithNavController
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    var isLight = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        preferences.apply {
            isLight = getBoolean("THEME", true)
        }

        if (isLight) {
            theme.applyStyle(R.style.AppTheme, true)
        } else {
            theme.applyStyle(R.style.DarkAppTheme, true)
        }

        setContentView(R.layout.activity_main)
        Fresco.initialize(this)

        setupBottomNavMenu()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavMenu()
    }

    private fun setupBottomNavMenu() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navGraphIds = listOf(
            R.navigation.trending,
            R.navigation.search,
            R.navigation.favorite,
            R.navigation.settings
        )
        bottomNav?.setupWithNavController(
            navGraphIds,
            supportFragmentManager,
            R.id.my_nav_host_fragment,
            intent
        )
    }
}
