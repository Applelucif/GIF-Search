package com.example.gyphyclient.model

data class Gif(
    val height: String,
    val size: String,
    val webp: String,
    val width: String,
    val hash: String
)

data class Thumbnail(
    val height: String,
    val size: String,
    val url: String,
    val width: String
)