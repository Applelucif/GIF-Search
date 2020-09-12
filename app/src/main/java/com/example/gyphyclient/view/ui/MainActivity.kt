package com.example.gyphyclient.view.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.example.gyphyclient.GiphyApplication
import com.example.gyphyclient.R
import com.example.gyphyclient.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {


    lateinit var favoriteFragment: TrendingFragment
    lateinit var searchFragment: SearchFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val fm = supportFragmentManager
        val transaction = fm
            .beginTransaction()
            .replace(R.id.fragment_container, TrendingFragment())
        transaction.commit()

        var bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.favorite -> {
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
            }
                true
            }
    }



}
