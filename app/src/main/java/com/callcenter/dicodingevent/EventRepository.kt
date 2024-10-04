package com.callcenter.dicodingevent

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EventRepository {
    private val apiService: ApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://event-api.dicoding.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    fun getEvents(status: Int): Call<EventResponse> {
        return apiService.getEvents(status)
    }

    fun searchEvents(status: Int, query: String): Call<EventResponse> {
        return apiService.searchEvents(status, query)
    }

    fun getEventDetails(eventId: Int): Call<EventResponse> {
        return apiService.getEventDetails(eventId)
    }
}
