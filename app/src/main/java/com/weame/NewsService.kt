package com.weame

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {
    @GET("top-headlines")
    fun getTopHeadlines(@Query("country") country: String = "us"): Call<NewsResponse>
}
