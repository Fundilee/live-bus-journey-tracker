package com.livebusjourneytracker.core.data.dto

import com.google.gson.annotations.SerializedName

data class BusRouteDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("lineStrings")
    val lineStrings: List<String>? = null,
    @SerializedName("lineStatuses")
    val lineStatuses: List<LineStatusDto>? = null,
    @SerializedName("routeSections")
    val routeSections: List<RouteSectionDto>? = null,
    @SerializedName("serviceTypes")
    val serviceTypes: List<ServiceTypeDto>? = null,
    @SerializedName("stations")
    val stations: List<StationsDto>? = null
)

data class StationsDto(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("stopType")
    val stopType: String

)

data class LineStatusDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("statusSeverity")
    val statusSeverity: Int,
    @SerializedName("statusSeverityDescription")
    val statusSeverityDescription: String,
    @SerializedName("reason")
    val reason: String? = null
)

data class RouteSectionDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("direction")
    val direction: String,
    @SerializedName("originator")
    val originator: String,
    @SerializedName("destination")
    val destination: String,
    @SerializedName("serviceType")
    val serviceType: String
)

data class ServiceTypeDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("uri")
    val uri: String
)