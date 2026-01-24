package com.livebusjourneytracker.core.data.dto

import com.google.gson.annotations.SerializedName

data class JourneyResponseDto(
    @SerializedName("\$type")
    val type: String,
    val toLocationDisambiguation: DisambiguationDto?,
    val fromLocationDisambiguation: DisambiguationDto?,
    val viaLocationDisambiguation: DisambiguationDto?,
    val recommendedMaxAgeMinutes: Int?,
    val searchCriteria: SearchCriteriaDto?,
    val journeyVector: JourneyVectorDto?
)

data class DisambiguationDto(
    @SerializedName("\$type")
    val type: String,
    val disambiguationOptions: List<DisambiguationOptionDto> = emptyList(),
    val matchStatus: String?
)

data class DisambiguationOptionDto(
    @SerializedName("\$type")
    val type: String,
    val parameterValue: String,
    val uri: String,
    val place: PlaceDto,
    val matchQuality: Int
)

data class PlaceDto(
    @SerializedName("\$type")
    val type: String,
    val naptanId: String?,
    val modes: List<String>?,
    val icsCode: String?,
    val stopType: String?,
    val url: String?,
    val commonName: String?,
    val placeType: String?,
    val additionalProperties: List<AdditionalPropertyDto> = emptyList(),
    val lat: Double?,
    val lon: Double?
)

data class AdditionalPropertyDto(
    val category: String?,
    val key: String?,
    val value: String?
)

data class SearchCriteriaDto(
    val dateTime: String?,
    val dateTimeType: String?
)

data class JourneyVectorDto(
    val from: String?,
    val to: String?,
    val via: String?,
    val uri: String?
)