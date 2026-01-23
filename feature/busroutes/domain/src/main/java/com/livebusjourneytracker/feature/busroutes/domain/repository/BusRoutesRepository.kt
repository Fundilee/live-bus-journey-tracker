package com.livebusjourneytracker.feature.busroutes.domain.repository

import com.livebusjourneytracker.feature.busroutes.domain.model.BusRoute
import com.livebusjourneytracker.feature.busroutes.domain.model.BusStop
import kotlinx.coroutines.flow.Flow

interface BusRoutesRepository {
    
    suspend fun getAllBusRoutes(): Flow<List<BusRoute>>
    
    suspend fun getBusRouteById(routeId: String): Flow<BusRoute?>
    
    suspend fun searchBusRoutes(query: String): Flow<List<BusRoute>>
    
    suspend fun getBusStopsForRoute(routeId: String): Flow<List<BusStop>>
    
    suspend fun getNearbyBusStops(latitude: Double, longitude: Double, radius: Int = 500): Flow<List<BusStop>>
    
    suspend fun refreshBusRoutes(): Result<Unit>
}