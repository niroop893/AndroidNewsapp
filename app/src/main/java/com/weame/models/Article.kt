package com.weame.models

data class Article(
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val source: String // Ensure this is the correct type
)

data class Source(
    val id: String?,
    val name: String
)
