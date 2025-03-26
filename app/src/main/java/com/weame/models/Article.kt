package com.weame.models

data class Article(
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,  // Add this field
    val source: String        // Add this field
)
