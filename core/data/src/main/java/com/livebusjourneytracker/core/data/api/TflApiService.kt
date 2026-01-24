package com.livebusjourneytracker.core.data.api

import com.livebusjourneytracker.core.data.dto.BusRouteDto
import com.livebusjourneytracker.core.data.dto.BusStopDto
import com.livebusjourneytracker.core.data.dto.JourneyResponseDto
import com.livebusjourneytracker.core.data.dto.SearchResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TflApiService {
    
    @GET("Line/Mode/bus")
    suspend fun getAllBusRoutes(): Response<List<BusRouteDto>>
    
    @GET("Line/{lineId}")
    suspend fun getBusRouteById(@Path("lineId") lineId: String): Response<List<BusRouteDto>>
    
    @GET("StopPoint/Search/{query}?modes=bus")
    suspend fun searchBusRoutes(@Path("query") query: String): Response<SearchResponseDto>

    @GET("Journey/JourneyResults/{from}/to/{to}")
    suspend fun journeyResults(@Path("from") from: String, @Path("to") to: String): Response<JourneyResponseDto>

    @GET("Line/{lineId}/StopPoints")
    suspend fun getStopPointsForLine(@Path("lineId") lineId: String): Response<List<BusStopDto>>
}