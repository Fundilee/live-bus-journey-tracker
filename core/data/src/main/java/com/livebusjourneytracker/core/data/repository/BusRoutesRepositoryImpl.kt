package com.livebusjourneytracker.core.data.repository

import com.livebusjourneytracker.core.data.api.TflApiService
import com.livebusjourneytracker.core.data.mapper.BusRouteMapper
import com.livebusjourneytracker.core.data.mapper.BusStopMapper
import com.livebusjourneytracker.core.data.mapper.JourneyMapper
import com.livebusjourneytracker.core.data.mapper.SearchMapper
import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.model.BusStop
import com.livebusjourneytracker.core.domain.model.Journey
import com.livebusjourneytracker.core.domain.repository.BusRoutesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BusRoutesRepositoryImpl(
    private val apiService: TflApiService
) : BusRoutesRepository {
    
    override suspend fun getAllBusRoutes(): Flow<List<BusRoute>> = flow {
        try {
            val response = apiService.getAllBusRoutes()
            if (response.isSuccessful) {
                val routes = response.body()?.let { BusRouteMapper.mapToDomainList(it) } ?: emptyList()
                emit(routes)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    override suspend fun getBusRouteById(routeId: String): Flow<BusRoute?> = flow {
        try {
            val response = apiService.getBusRouteById(routeId)
            if (response.isSuccessful) {
                val route = response.body()?.firstOrNull()?.let { BusRouteMapper.mapToDomain(it) }
                emit(route)
            } else {
                emit(null)
            }
        } catch (e: Exception) {
            emit(null)
        }
    }

    override suspend fun getAllBusJourneys(from: String, to: String): Flow<List<BusRoute>> = flow {
        try {
            val response = apiService.journeyResults(from, to)
            if (response.isSuccessful) {
                val journeyResponse = response.body()
                // For now, return empty list as this endpoint returns disambiguation data, not bus routes
                // In a real implementation, you'd parse the journey response differently
                emit(emptyList())
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    override suspend fun searchBusRoutes(query: String): Flow<List<BusRoute>> = flow {
        try {
            val response = apiService.searchBusRoutes(query)
            if (response.isSuccessful) {
                val searchResponse = response.body()
                val routes = searchResponse?.matches?.let { SearchMapper.mapMatchedStopsToBusRoutes(it) } ?: emptyList()
                emit(routes)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    override suspend fun getBusStopsForRoute(routeId: String): Flow<List<BusStop>> = flow {
        try {
            val response = apiService.getStopPointsForLine(routeId)
            if (response.isSuccessful) {
                val stops = response.body()?.let { BusStopMapper.mapToDomainList(it) } ?: emptyList()
                emit(stops)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    override suspend fun getNearbyBusStops(latitude: Double, longitude: Double, radius: Int): Flow<List<BusStop>> = flow {
        try {
            // For now, emit some sample stops around London
            // In a real implementation, you'd call the TfL API for nearby stops
            val sampleStops = listOf(
                BusStop(
                    id = "490000001E",
                    name = "Oxford Circus",
                    commonName = "Oxford Circus",
                    distance = 100.0,
                    bearing = "NE",
                    naptanId = "490000001E",
                    lat = 51.5154,
                    lon = -0.1441,
                    stopType = "NaptanBusCoachStation",
                    zone = "1",
                    lines = listOf("3", "6", "12", "23")
                ),
                BusStop(
                    id = "490000002E",
                    name = "Regent Street",
                    commonName = "Regent Street",
                    distance = 200.0,
                    bearing = "SE",
                    naptanId = "490000002E",
                    lat = 51.5118,
                    lon = -0.1395,
                    stopType = "NaptanBusCoachStation",
                    zone = "1",
                    lines = listOf("3", "6", "12")
                )
            )
            emit(sampleStops)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    override suspend fun planJourney(from: String, to: String): Flow<Journey?> = flow {
        try {
            val response = apiService.journeyResults(from, to)
            if (response.isSuccessful) {
                val journey = response.body()?.let { JourneyMapper.mapToDomain(it) }
                emit(journey)
            } else {
                // Emit null on API failure
                emit(null)
            }
        } catch (e: Exception) {
            // Emit null on exception
            emit(null)
        }
    }

    override suspend fun refreshBusRoutes(): Result<Unit> {
        return try {
            val response = apiService.getAllBusRoutes()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to refresh: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}