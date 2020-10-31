package com.example.gyphyclient.view.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.gyphyclient.R
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var navGraph: NavGraph
    var isLight = true
    var neededFragment = "TrendingFragment"

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        preferences.apply {
            isLight = getBoolean("THEME", true)
            neededFragment = getString("TAG", "TrendingFragment").toString()
        }

        if (isLight) {
            theme.applyStyle(R.style.AppTheme, true)
        } else {
            theme.applyStyle(R.style.DarkAppTheme, true)
        }

        setContentView(R.layout.activity_main)
        Fresco.initialize(this)

        val host = my_nav_host_fragment as NavHostFragment
        val graphInflater = host.navController.navInflater
        navGraph = graphInflater.inflate(R.navigation.nav_graph)

        val navController = host.navController

        if (neededFragment == "SettingsFragment") {
            navGraph.startDestination = R.id.settingsFragment
            navController.graph = navGraph
        } else {
            navGraph.startDestination = R.id.trendingFragment
            navController.graph = navGraph
        }

        val editor = preferences.edit()
        editor
            .putString("TAG", "TrendingFragment")
            .apply()

        setupBottomNavMenu(navController)
    }

    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.setupWithNavController(navController)
    }
}
