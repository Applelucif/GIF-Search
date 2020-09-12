package com.example.gyphyclient

import android.app.Application
import android.os.Parcel
import android.os.Parcelable
import com.example.gyphyclient.data.database.TrendingDatabase

class GiphyApplication : Application(), Parcelable {
    companion object {
        lateinit var instance: GiphyApplication
        lateinit var database: TrendingDatabase
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        database = TrendingDatabase.invoke(this)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }
}