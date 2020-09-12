package com.example.gyphyclient.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Single

@Dao
interface DataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(data: List<DataEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearchData(data: List<DataSearchEntity>)

    @Query("SELECT * from data")
    fun queryData(): Single<List<DataEntity>>

    @Query("SELECT * from searchData where searchText = :searchText")
    fun queryData(searchText:String): Single<List<DataSearchEntity>>
}