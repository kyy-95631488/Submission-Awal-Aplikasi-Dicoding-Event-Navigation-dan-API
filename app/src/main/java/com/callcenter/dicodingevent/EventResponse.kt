package com.callcenter.dicodingevent

import com.google.gson.annotations.SerializedName

data class EventResponse(
    val error: Boolean,
    val message: String,
    val event: Event,
    @SerializedName("listEvents") val listEvents: List<Event>? = null
)