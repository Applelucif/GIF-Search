package com.example.gyphyclient.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface DataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(data: List<DataEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavoriteData(data: DataFavoriteEntity)

    @Query("SELECT * from data")
    fun queryData(): Flowable<List<DataEntity>>

    @Query("SELECT COUNT(hash) from data where hash = :hash")
    fun queryDataHash(hash:String): Int

    @Query("SELECT * from favoriteData")
    fun queryFavoriteData(): Flowable<List<DataFavoriteEntity>>
}