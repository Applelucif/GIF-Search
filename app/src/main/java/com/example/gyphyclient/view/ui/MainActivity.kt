package com.example.gyphyclient.view.ui

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gyphyclient.R
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val fragmentTrending = TrendingFragment()
    private val fragmentSearch = SearchFragment()
    private val fragmentFavorite = FavoriteFragment()
    private val fragmentSettings = SettingsFragment()
    private var active: Fragment = fragmentTrending
    private val fm = supportFragmentManager
    var isLight = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
/*        val editor = preferences.edit()

        editor
            .putString("TAG", TrendingFragment.TAG)
            .apply()*/

        preferences.apply {
            isLight = getBoolean("THEME", false)
        }
        if (isLight) {
            theme.applyStyle(R.style.AppTheme, true)
        } else {
            theme.applyStyle(R.style.DarkAppTheme, true)
        }

        setContentView(R.layout.activity_main)
        Fresco.initialize(this)

        fm.beginTransaction().add(R.id.fragment_container, fragmentSettings, "4")
            .hide(fragmentSettings)
            .commit()
        fm.beginTransaction().add(R.id.fragment_container, fragmentFavorite, "3")
            .hide(fragmentFavorite)
            .commit()
        fm.beginTransaction().add(R.id.fragment_container, fragmentSearch, "2")
            .hide(fragmentSearch)
            .commit()
        fm.beginTransaction().add(R.id.fragment_container, fragmentTrending, "1")
            .commit()

        setBottomNavigationView()
        onResume()
    }

    override fun onPause() {
        super.onPause()
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = preferences.edit()
        when (active) {
            fragmentSettings -> {
                editor
                    .putString("TAG", SettingsFragment.TAG)
                    .apply()
            }
            fragmentTrending -> {
                editor
                    .putString("TAG", TrendingFragment.TAG)
                    .apply()
            }
            fragmentSearch -> {
                editor
                    .putString("TAG", SearchFragment.TAG)
                    .apply()
            }
            fragmentFavorite -> {
                editor
                    .putString("TAG", FavoriteFragment.TAG)
                    .apply()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        var activeFragment = ""
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        preferences.apply {
            activeFragment = getString("TAG", "").toString()
        }
        when (activeFragment) {
            "SettingsFragment" -> {
                fm
                    .beginTransaction()
                    .hide(active)
                    .show(fragmentSettings)
                    .commit()
                active = fragmentSettings
            }
            "FavoriteFragment" -> {
                fm
                    .beginTransaction()
                    .hide(active)
                    .show(fragmentFavorite)
                    .commit()
                active = fragmentFavorite
            }
            "SearchFragment" -> {
                fm
                    .beginTransaction()
                    .hide(active)
                    .show(fragmentSearch)
                    .commit()
                active = fragmentSearch
            }
            "TrendingFragment" -> {
                fm
                    .beginTransaction()
                    .hide(active)
                    .show(fragmentTrending)
                    .commit()
                active = fragmentTrending
            }
        }
    }

    private fun setBottomNavigationView() {
        var bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.top -> {
                    fm
                        .beginTransaction()
                        .hide(active)
                        .show(fragmentTrending)
                        .commit()
                    active = fragmentTrending
                }
                R.id.search -> {
                    fm
                        .beginTransaction()
                        .hide(active)
                        .show(fragmentSearch)
                        .commit()
                    active = fragmentSearch
                }
                R.id.favorite -> {
                    fm
                        .beginTransaction()
                        .hide(active)
                        .show(fragmentFavorite)
                        .commit()
                    active = fragmentFavorite
                }
                R.id.settings -> {
                    fm
                        .beginTransaction()
                        .hide(active)
                        .show(fragmentSettings)
                        .commit()
                    active = fragmentSettings
                }
            }
            true
        }
    }
}
