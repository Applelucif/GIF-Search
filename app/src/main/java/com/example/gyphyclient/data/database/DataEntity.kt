package com.example.gyphyclient.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "data")
data class DataEntity(
    @ColumnInfo(name = "images")
    val images: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "username")
    val username: String,
    @PrimaryKey
    @ColumnInfo(name = "hash")
    val hash: String,
    @ColumnInfo(name = "smallImage")
    val smallImage: String
)

@Entity(tableName = "searchData")
data class DataSearchEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "images")
    val images: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "searchText")
    val searchText: String,
    @ColumnInfo(name = "hash")
    val hash: String,
    @ColumnInfo(name = "smallImage")
    val smallImage: String
)

@Entity(tableName = "favoriteData")
data class DataFavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "images")
    val images: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "searchText")
    val searchText: String,
    @ColumnInfo(name = "hash")
    val hash: String,
    @ColumnInfo(name = "smallImage")
    val smallImage: String
)