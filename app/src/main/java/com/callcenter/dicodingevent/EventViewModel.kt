package com.callcenter.dicodingevent

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventViewModel : ViewModel() {

    private val repository = EventRepository()

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> get() = _events

    private val _event = MutableLiveData<Event>()
    val event: LiveData<Event> get() = _event

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchEvents(status: Int) {
        _isLoading.value = true
        _errorMessage.value = null
        repository.getEvents(status).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val eventResponse = response.body()
                    _events.value = eventResponse?.listEvents ?: emptyList()
                } else {
                    _events.value = emptyList()
                    _errorMessage.value = "Terjadi kesalahan API: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _events.value = emptyList()
                _errorMessage.value = "Tidak ada koneksi internet atau kesalahan server"
                Log.e("EventViewModel", "Error: ${t.message}", t)
            }
        })
    }

    fun searchEvents(status: Int, query: String) {
        _isLoading.value = true
        _errorMessage.value = null
        repository.searchEvents(status, query).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val eventResponse = response.body()
                    _events.value = eventResponse?.listEvents ?: emptyList()
                } else {
                    _events.value = emptyList()
                    _errorMessage.value = "Terjadi kesalahan API: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _events.value = emptyList()
                _errorMessage.value = "Tidak ada koneksi internet atau kesalahan server"
                Log.e("EventViewModel", "Error: ${t.message}", t)
            }
        })
    }

    fun fetchEventDetails(eventId: Int) {
        _isLoading.value = true
        Log.d("EventViewModel", "Loading started")
        _errorMessage.value = null
        repository.getEventDetails(eventId).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                Log.d("EventViewModel", "Loading ended")
                if (response.isSuccessful) {
                    val eventResponse = response.body()
                    if (eventResponse != null) {
                        val event = eventResponse.event
                        _event.value = event
                        Log.d("EventViewModel", "Event details fetched successfully: $event")
                    } else {
                        _event.value = null
                        Log.e("EventViewModel", "Event response is null")
                    }
                } else {
                    Log.e("EventViewModel", "Failed to fetch event details: ${response.message()}")
                    _errorMessage.value = "Terjadi kesalahan API: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _event.value = null
                _errorMessage.value = "Tidak ada koneksi internet atau kesalahan server"
                Log.e("EventViewModel", "Error: ${t.message}", t)
            }
        })
    }

    fun register(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    }
}
