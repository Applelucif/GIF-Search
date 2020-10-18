package com.example.gyphyclient.view.ui

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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

        rating_radio_group.setOnCheckedChangeListener() { radioGroup: RadioGroup, checkedId: Int ->
            when (checkedId) {
                R.id.g_rating -> setRating("G")
                R.id.pg_rating -> setRating("PG")
                R.id.pg13_rating -> setRating("PG-13")
                R.id.r_rating -> setRating("R")
            }
        }


        switch1.setOnCheckedChangeListener() { compoundButton: CompoundButton, b: Boolean ->
            Toast.makeText(
                context, "Положение: " + if (b) "справа" else "слева",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun setRating(rating: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()

        editor
            .putString("RATING", rating)
            .apply()
        }
}