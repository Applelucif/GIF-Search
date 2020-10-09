package com.example.gyphyclient.view.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gyphyclient.R
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val fragmentTranding = TrendingFragment()
        val fragmentSearch = SearchFragment()
        val fragmentFavorite = FavoriteFragment()
        var active: Fragment = fragmentTranding

        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.activity_main)

        val fm = supportFragmentManager
    
        var bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        fm.beginTransaction().add(R.id.fragment_container, fragmentFavorite, "3")
            .hide(fragmentFavorite).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragmentSearch, "2").hide(fragmentSearch)
            .commit();
        fm.beginTransaction().add(R.id.fragment_container, fragmentTranding, "1").commit();

        //TODO вынести в отдельный метод
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.top -> {
                    fm
                        .beginTransaction()
                        .hide(active)
                        .show(fragmentTranding)
                        .commit()
                    active = fragmentTranding
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
            }
            true
        }
    }
}
