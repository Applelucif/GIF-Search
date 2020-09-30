package com.example.gyphyclient.data.database

import com.example.gyphyclient.model.Data
import com.example.gyphyclient.model.Gif
import com.example.gyphyclient.model.Images
import com.example.gyphyclient.model.Thumbnail

fun DataEntity.toData() = Data(
    Images(Gif("320", "1024", this.images, "420", this.hash), Thumbnail("320", "1024", this.smallImage, "420")),
    this.title,
    this.type,
    this.username
)

fun DataSearchEntity.toData() = Data(
    Images(Gif("320", "1024", this.images, "420", this.hash), Thumbnail("320", "1024", this.smallImage, "420")),
    this.title,
    this.type,
    this.username
)

fun DataFavoriteEntity.toData() = Data(
    Images(Gif("320", "1024", this.images, "420", this.hash), Thumbnail("320", "1024", this.smallImage, "420")),
    this.title,
    this.type,
    this.username
)


@JvmName("toDataListDataEntity")
fun List<DataEntity>.toDataList() = this.map { it.toData() }
@JvmName("toDataListDataSearchEntity")
fun List<DataSearchEntity>.toDataList() = this.map { it.toData() }
fun List<DataFavoriteEntity>.toDataList() = this.map { it.toData() }


fun Data.toDataEntity() = DataEntity(
    images = this.images.original?.url ?: "empty url",
    smallImage = this.images.fixed_height_small_still?.smallImage ?: "empty url",
    title = this.title,
    type = this.type,
    username = this.username,
    hash = this.images.original?.hash ?: "empty hash"
)

fun Data.toDataEntity(searchText:String) = DataSearchEntity(
    images = this.images.original?.url ?: "empty url",
    smallImage = this.images.fixed_height_small_still?.smallImage ?: "empty url",
    title = this.title,
    type = this.type,
    username = this.username,
    searchText = searchText,
    hash = this.images.original?.hash ?: "empty hash"
)

fun Data.toDataFavoriteEntity() = DataFavoriteEntity(
    images = this.images.original?.url ?: "empty url",
    smallImage = this.images.fixed_height_small_still?.smallImage ?: "empty url",
    title = this.title,
    type = this.type,
    username = this.username,
    hash = this.images.original?.hash ?: "empty hash"
)

fun List<Data>.toDataEntityList() = this.map { it.toDataEntity() }
fun List<Data>.toSearchDataEntityList(searchText: String) = this.map { it.toDataEntity(searchText) }