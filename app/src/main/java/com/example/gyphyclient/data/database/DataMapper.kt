package com.example.gyphyclient.data.database

import com.example.gyphyclient.model.Data
import com.example.gyphyclient.model.FixedHeightSmallStill
import com.example.gyphyclient.model.Images

fun DataEntity.toData() = Data(
    Images(FixedHeightSmallStill("320", "1024", this.images, "420")),
    this.title,
    this.type,
    this.username
)

fun DataSearchEntity.toData() = Data(
    Images(FixedHeightSmallStill("320", "1024", this.images, "420")),
    this.title,
    this.type,
    this.username
)


@JvmName("toDataListDataEntity")
fun List<DataEntity>.toDataList() = this.map { it.toData() }
fun List<DataSearchEntity>.toDataList() = this.map { it.toData() }


fun Data.toDataEntity() = DataEntity(
    images = this.images.downsized_large?.url ?: "empty url",
    title = this.title,
    type = this.type,
    username = this.username
)

fun Data.toDataEntity(searchText:String) = DataSearchEntity(
    images = this.images.downsized_large?.url ?: "empty url",
    title = this.title,
    type = this.type,
    username = this.username,
    searchText = searchText
)

fun List<Data>.toDataEntityList() = this.map { it.toDataEntity() }
fun List<Data>.toSearchDataEntityList(searchText: String) = this.map { it.toDataEntity(searchText) }