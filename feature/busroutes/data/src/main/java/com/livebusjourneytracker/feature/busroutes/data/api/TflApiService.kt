package com.livebusjourneytracker.feature.busroutes.data.api

import com.livebusjourneytracker.feature.busroutes.data.dto.BusRouteDto
import com.livebusjourneytracker.feature.busroutes.data.dto.BusStopDto
import com.livebusjourneytracker.feature.busroutes.data.dto.SearchResponseDto
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
    
    @GET("Line/{lineId}/StopPoints")
    suspend fun getStopPointsForLine(@Path("lineId") lineId: String): Response<List<BusStopDto>>
}