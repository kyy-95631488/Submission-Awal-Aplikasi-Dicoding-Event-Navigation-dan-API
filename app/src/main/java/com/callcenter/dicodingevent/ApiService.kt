package com.callcenter.dicodingevent

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvents(@Query("active") status: Int): Call<EventResponse>

    @GET("events")
    fun searchEvents(@Query("active") status: Int, @Query("q") query: String): Call<EventResponse>

    @GET("events/{id}")
    fun getEventDetails(@Path("id") eventId: Int): Call<EventResponse>
}
