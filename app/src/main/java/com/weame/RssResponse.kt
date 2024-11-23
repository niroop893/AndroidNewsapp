package com.weame
import com.google.gson.annotations.SerializedName


data class RssResponse(
    @SerializedName("channel")
    val channel: Channel
)

data class Channel(
    @SerializedName("item")
    val items: List<NewsItem>
)

data class NewsItem(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("link")
    val url: String
)
