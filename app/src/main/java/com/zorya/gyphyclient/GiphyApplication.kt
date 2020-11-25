package com.example.gyphyclient

import android.app.Application
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.example.gyphyclient.data.database.TrendingDatabase
import com.google.firebase.FirebaseApp

class GiphyApplication : Application(), Parcelable {

    companion object {
        lateinit var instance: GiphyApplication
        lateinit var database: TrendingDatabase

        fun getAppContext(): Context {
            return instance
        }
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        // Obtain the FirebaseAnalytics instance.
        FirebaseApp.initializeApp(this)
        database = TrendingDatabase.invoke(this)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
    }

    override fun describeContents(): Int {
        return 0
    }
}