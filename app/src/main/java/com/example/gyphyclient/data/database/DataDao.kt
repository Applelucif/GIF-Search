package com.example.gyphyclient.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface DataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(data: List<DataEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearchData(data: List<DataSearchEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavoriteData(data: DataEntity)

    @Query("SELECT * from data")
    fun queryData(): Single<List<DataEntity>>

    @Query("SELECT * from favoriteData")
    fun queryFavoriteData(): Single<List<DataFavoriteEntity>>

    @Query("SELECT * from searchData where searchText = :searchText")
    fun queryData(searchText:String): Single<List<DataSearchEntity>>

    @Query("SELECT COUNT(hash) from data where hash = :hash")
    fun queryDataHash(hash:String): Int
}