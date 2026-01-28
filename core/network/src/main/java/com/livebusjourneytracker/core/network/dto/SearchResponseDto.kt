package com.livebusjourneytracker.core.network.dto

import com.google.gson.annotations.SerializedName

data class SearchResponseDto(
    @SerializedName("\$type")
    val type: String,
    val query: String,
    val total: Int,
    val matches: List<MatchedStopDto>
)

data class MatchedStopDto(
    @SerializedName("\$type")
    val type: String,
    val icsId: String?,
    val modes: List<String>,
    val zone: String?,
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double
)