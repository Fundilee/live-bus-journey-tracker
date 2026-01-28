package com.livebusjourneytracker.core.network.dto

import com.google.gson.annotations.SerializedName

data class JourneyResponseDto(
    @SerializedName("\$type")
    val type: String,
    @SerializedName("toLocationDisambiguation")
    val toLocationDisambiguation: DisambiguationDto?,
    @SerializedName("fromLocationDisambiguation")
    val fromLocationDisambiguation: DisambiguationDto?,
    @SerializedName("viaLocationDisambiguation")
    val viaLocationDisambiguation: DisambiguationDto?,
    @SerializedName("recommendedMaxAgeMinutes")
    val recommendedMaxAgeMinutes: Int?,
    @SerializedName("searchCriteria")
    val searchCriteria: SearchCriteriaDto?,
    @SerializedName("journeyVector")
    val journeyVector: JourneyVectorDto?,
    @SerializedName("journeys")
    val journeys: List<JourneyDto>? = null
)

data class DisambiguationDto(
    @SerializedName("\$type")
    val type: String,
    @SerializedName("disambiguationOptions")
    val disambiguationOptions: List<DisambiguationOptionDto>? = null,
    @SerializedName("matchStatus")
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

data class JourneyDto (
    val startDateTime: String,
    val arrivalDateTime: String,
    val duration: Int,
    val legs: List<LegDto>
)

data class LegDto(
    val duration: Int?,
    val departureTime: String?,
    val arrivalTime: String?,
    val departurePoint: PlaceDto,
    val arrivalPoint: PlaceDto,
    val mode: ModeDto?,
    val routeOptions: List<RouteOptionDto>?,
    val lineId: String?
)

data class ModeDto(
    val id: String?,
    val name: String?
)

data class RouteOptionDto(
    val name: String?,
)

data class PlaceDto(
    @SerializedName("\$type")
    val type: String,
    val naptanId: String?,
    val icsCode: String?,
    val stopType: String?,
    val url: String?,
    val commonName: String?,
    val placeType: String?,
    val additionalProperties: List<AdditionalPropertyDto>? = null,
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