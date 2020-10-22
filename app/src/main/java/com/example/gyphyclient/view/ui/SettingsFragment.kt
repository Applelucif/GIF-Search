package com.example.gyphyclient.view.ui

import android.app.Application
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.example.gyphyclient.GiphyApplication
import com.example.gyphyclient.R
import kotlinx.android.synthetic.main.settings_fragment.*

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)

        val preferences =
            PreferenceManager.getDefaultSharedPreferences(GiphyApplication.getAppContext())
        preferences.apply {
            switch1.isChecked = getBoolean("THEME", false)
        }

        rating_radio_group.setOnCheckedChangeListener() { _: RadioGroup, checkedId: Int ->
            when (checkedId) {
                R.id.g_rating -> setRating("G")
                R.id.pg_rating -> setRating("PG")
                R.id.pg13_rating -> setRating("PG-13")
                R.id.r_rating -> setRating("R")
            }
        }

        switch1.setOnCheckedChangeListener() { _: CompoundButton, isLight: Boolean ->
            setTheme(isLight)
        }
    }

    private fun setRating(rating: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()

        editor
            .putString("RATING", rating)
            .apply()
    }

    private fun setTheme(isLight: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()

        editor
            .putBoolean("THEME", isLight)
            .apply()

        val intent =  activity?.intent // from getIntent()
        intent?.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        activity?.finish()
        startActivity(intent)
    }

    companion object {
        const val TAG = "SettingsFragment"
    }
}