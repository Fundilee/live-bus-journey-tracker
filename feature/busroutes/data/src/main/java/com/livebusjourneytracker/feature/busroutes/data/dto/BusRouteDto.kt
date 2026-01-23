package com.livebusjourneytracker.feature.busroutes.data.dto

import com.google.gson.annotations.SerializedName

data class BusRouteDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("lineStatuses")
    val lineStatuses: List<LineStatusDto>? = null,
    @SerializedName("routeSections")
    val routeSections: List<RouteSectionDto>? = null,
    @SerializedName("serviceTypes")
    val serviceTypes: List<ServiceTypeDto>? = null
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