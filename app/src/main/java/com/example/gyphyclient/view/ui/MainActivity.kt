package com.example.gyphyclient.view.ui

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.activity_main)

        fm.beginTransaction().add(R.id.fragment_container, fragmentSettings, "4")
            .hide(fragmentSettings)
            .commit()
        fm.beginTransaction().add(R.id.fragment_container, fragmentFavorite, "3")
            .hide(fragmentFavorite)
            .commit()
        fm.beginTransaction().add(R.id.fragment_container, fragmentSearch, "2")
            .hide(fragmentSearch)
            .commit();
        fm.beginTransaction().add(R.id.fragment_container, fragmentTrending, "1")
            .commit();

        setBottomNavigationView()
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
                    fragmentFavorite.backToFavoriteFragment()
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
