package com.weame

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {
    @GET("https://news.google.com/rss")
    fun getGoogleNews(): Call<RssResponse>
}
