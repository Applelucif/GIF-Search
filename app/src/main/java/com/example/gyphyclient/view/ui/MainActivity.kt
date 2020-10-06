package com.example.gyphyclient.view.ui

import android.graphics.Point
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gyphyclient.R
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.activity_main)

        val fm = supportFragmentManager
        val transaction = fm
            .beginTransaction()
            .replace(R.id.fragment_container, TrendingFragment())
        transaction.commit()

        var bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.top -> {
                    val fmTrending = supportFragmentManager
                    val transaction = fmTrending
                        .beginTransaction()
                        .replace(R.id.fragment_container, TrendingFragment())
                    transaction.commit()
                }
                R.id.search -> {
                    val fmSearch = supportFragmentManager
                    val transaction = fmSearch
                        .beginTransaction()
                        .replace(R.id.fragment_container, SearchFragment())
                    transaction.commit()
                }
                R.id.favorite -> {
                    val fmSearch = supportFragmentManager
                    val transaction = fmSearch
                        .beginTransaction()
                        .replace(R.id.fragment_container, FavoriteFragment())
                    transaction.commit()
                }
            }
                true
            }
    }



}
