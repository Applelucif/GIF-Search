package com.zorya.gyphyclient.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zorya.gyphyclient.internal.DATABASE_NAME

@Database(entities = [DataEntity::class, DataFavoriteEntity::class], version = 1)
abstract class TrendingDatabase : RoomDatabase() {
    abstract fun dataDao(): DataDao

    companion object {
        @Volatile // All threads have immediate access to this property
        private var instance: TrendingDatabase? = null
        private val LOCK = Any()

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TrendingDatabase::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration().build()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }
    }
}