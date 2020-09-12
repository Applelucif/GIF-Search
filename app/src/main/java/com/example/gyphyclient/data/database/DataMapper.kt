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

fun List<DataEntity>.toDataList() = this.map { it.toData() }

fun Data.toDataEntity() = DataEntity(
    images = this.images.downsized_large?.url ?: "empty url",
    title = this.title,
    type = this.type,
    username = this.username
)

fun List<Data>.toDataEntityList() = this.map { it.toDataEntity() }