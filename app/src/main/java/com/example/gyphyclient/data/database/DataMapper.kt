package com.example.gyphyclient.data.database

import com.example.gyphyclient.model.Data
import com.example.gyphyclient.model.Gif
import com.example.gyphyclient.model.Images
import com.example.gyphyclient.model.Thumbnail

fun DataEntity.toData() = Data(
    Images(Gif(this.height, "1024", this.gif ,this.images, this.width, this.hash), Thumbnail("320", "1024", this.smallImage, "420")),
    this.title,
    this.type,
    this.username
)

fun DataFavoriteEntity.toData() = Data(
    Images(Gif(this.height, "1024", this.gif, this.images, this.width, this.hash), Thumbnail("320", "1024", this.smallImage, "420")),
    this.title,
    this.type,
    this.username
)


@JvmName("toDataListDataEntity")
fun List<DataEntity>.toDataList() = this.map { it.toData() }
fun List<DataFavoriteEntity>.toDataList() = this.map { it.toData() }


fun Data.toDataEntity() = DataEntity(
    height = this.images.original?.height ?: "empty height",
    width = this.images.original?.width ?: "empty width",
    images = this.images.original?.webp ?: "empty url webp",
    gif = this.images.original?.url ?: "empty url gif",
    smallImage = this.images.fixed_height_small_still?.url ?: "empty url smallImage",
    title = this.title,
    type = this.type,
    username = this.username,
    hash = this.images.original?.hash ?: "empty hash"
)

fun Data.toDataFavoriteEntity() = DataFavoriteEntity(
    height = this.images.original?.height ?: "empty height",
    width = this.images.original?.width ?: "empty width",
    images = this.images.original?.webp ?: "empty url webp",
    gif = this.images.original?.url ?: "empty url gif",
    smallImage = this.images.fixed_height_small_still?.url ?: "empty url smallImage",
    title = this.title,
    type = this.type,
    username = this.username,
    hash = this.images.original?.hash ?: "empty hash"
)

fun List<Data>.toDataEntityList() = this.map { it.toDataEntity() }