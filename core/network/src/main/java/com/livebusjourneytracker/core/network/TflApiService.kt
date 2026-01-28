package com.livebusjourneytracker.core.network

import com.livebusjourneytracker.core.network.dto.BusArrivalDto
import com.livebusjourneytracker.core.network.dto.BusRouteDto
import com.livebusjourneytracker.core.network.dto.JourneyResponseDto
import com.livebusjourneytracker.core.network.dto.SearchResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TflApiService {

    @GET("/Line/{lineId}/Arrivals")
    suspend fun getBusRouteArrivalsById(@Path("lineId") lineId: String): Response<List<BusArrivalDto>>

    @GET("StopPoint/Search/{query}?modes=bus")
    suspend fun searchBusRoutes(@Path("query") query: String): Response<SearchResponseDto>

    @GET("Journey/JourneyResults/{from}/to/{to}")
    suspend fun journeyResults(
        @Path("from") from: String,
        @Path("to") to: String
    ): Response<JourneyResponseDto>

    @GET("/Line/{lineId}/Route/Sequence/outbound")
    suspend fun getOutboundRouteSequence(@Path("lineId") lineId: String): Response<BusRouteDto>
}