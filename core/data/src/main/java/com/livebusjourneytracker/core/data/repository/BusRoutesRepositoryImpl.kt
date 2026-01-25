package com.livebusjourneytracker.core.data.repository

import com.livebusjourneytracker.core.data.api.TflApiService
import com.livebusjourneytracker.core.data.mapper.ArrivalBusStopMapper
import com.livebusjourneytracker.core.data.mapper.BusRouteMapper
import com.livebusjourneytracker.core.data.mapper.BusStopMapper
import com.livebusjourneytracker.core.data.mapper.JourneyMapper
import com.livebusjourneytracker.core.data.mapper.SearchMapper
import com.livebusjourneytracker.core.domain.model.ArrivalBusStop
import com.livebusjourneytracker.core.domain.model.BusJourney
import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.model.BusStop
import com.livebusjourneytracker.core.domain.model.Journey
import com.livebusjourneytracker.core.domain.repository.BusRoutesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BusRoutesRepositoryImpl(
    private val apiService: TflApiService
) : BusRoutesRepository {

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

    override suspend fun planJourney(from: String, to: String): Flow<BusJourney?> = flow {
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

    override suspend fun getBusRouteArrivalsById(lineId: String): Flow<List<ArrivalBusStop>> = flow {
        try {
            val response = apiService.getBusRouteArrivalsById(lineId)
            if (response.isSuccessful) {
                val arrivalBusStops = response.body()?.let { ArrivalBusStopMapper.mapToDomainList(it) } ?: emptyList()
                emit(arrivalBusStops)
            } else {
                // Emit null on API failure
                emit(emptyList())
            }
        } catch (e: Exception) {
            // Emit null on exception
            emit(emptyList())
        }
    }

    override suspend fun getRouteSequence(lineId: String): Flow<List<BusRoute>> = flow {
        try {
            val response = apiService.getRouteSequence(lineId)
            if (response.isSuccessful) {
                val routeSequence = response.body()?.let { BusRouteMapper.mapToDomainList(it) } ?: emptyList()
                emit(routeSequence)
            } else {
                // Emit null on API failure
                emit(emptyList())
            }
        } catch (e: Exception) {
            // Emit null on exception
            emit(emptyList())
        }
    }
//    override suspend fun refreshBusRoutes(): Result<Unit> {
//        return try {
//            val response = apiService.()
//            if (response.isSuccessful) {
//                Result.success(Unit)
//            } else {
//                Result.failure(Exception("Failed to refresh: ${response.message()}"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
}