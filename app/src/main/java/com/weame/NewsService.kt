package com.weame

import com.weame.models.RssResponse  // Ensure this import is here
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {
    @GET("rss/search")
    fun getGoogleNews(
        @Query("q") query: String,
        @Query("hl") hl: String = "en-IN",
        @Query("gl") gl: String = "IN",
        @Query("ceid") ceid: String = "IN:en"
    ): Call<RssResponse>
}
