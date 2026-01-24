package com.livebusjourneytracker.core.domain.repository

import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.model.BusStop
import com.livebusjourneytracker.core.domain.model.Journey
import kotlinx.coroutines.flow.Flow

interface BusRoutesRepository {
    
    suspend fun getAllBusRoutes(): Flow<List<BusRoute>>
    
    suspend fun getBusRouteById(routeId: String): Flow<BusRoute?>
    
    suspend fun searchBusRoutes(query: String): Flow<List<BusRoute>>
    
    suspend fun getBusStopsForRoute(routeId: String): Flow<List<BusStop>>
    
    suspend fun getNearbyBusStops(latitude: Double, longitude: Double, radius: Int = 500): Flow<List<BusStop>>

    suspend fun getAllBusJourneys(from: String, to: String): Flow<List<BusRoute>>
    
    suspend fun planJourney(from: String, to: String): Flow<Journey?>
    
    suspend fun refreshBusRoutes(): Result<Unit>
}