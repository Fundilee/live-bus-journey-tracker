package com.livebusjourneytracker.core.data.repository

import android.util.Log
import com.livebusjourneytracker.core.data.api.TflApiService
import com.livebusjourneytracker.core.data.dto.JourneyResponseDto
import com.livebusjourneytracker.core.data.mapper.BusArrivalMapper
import com.livebusjourneytracker.core.data.mapper.BusJourneyMapper
import com.livebusjourneytracker.core.data.mapper.BusRouteMapper
import com.livebusjourneytracker.core.data.mapper.SearchMapper
import com.livebusjourneytracker.core.domain.model.BusArrival
import com.livebusjourneytracker.core.domain.model.BusJourney
import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.model.requiresDisambiguation
import com.livebusjourneytracker.core.domain.repository.BusRoutesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BusRoutesRepositoryImpl(
    private val apiService: TflApiService
) : BusRoutesRepository {

    override suspend fun searchBusRoutes(query: String): Flow<List<BusRoute>> = flow {
        try {
            val response = apiService.searchBusRoutes(query)
            if (response.isSuccessful) {
                val searchResponse = response.body()
                val routes =
                    searchResponse?.matches?.let { SearchMapper.mapMatchedStopsToBusRoutes(it) }
                        ?: emptyList()
                emit(routes)
            } else {
                emit(emptyList())
            }
        } catch (_: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun planJourney(from: String, to: String): Flow<BusJourney?> = flow {
        try {
            val response = apiService.journeyResults(from, to)
            Log.d("API_RESPONSE", "Response code: ${response.code()}")
            
            // Handle both 200 (success) and 300 (disambiguation) responses
            when {
                response.isSuccessful -> {
                    // 200 response - normal journey results
                    val journey = response.body()?.let { BusJourneyMapper.mapToDomain(it) }
                    Log.d("API_RESPONSE", "200 response - Journey: ${journey?.journey?.size} routes")
                    emit(journey)
                }
                response.code() == 300 -> {
                    // 300 response - disambiguation needed, data is in errorBody
                    val errorBodyContent = response.errorBody()?.string()
                    if (errorBodyContent != null) {
                        try {
                            val dto = com.google.gson.Gson().fromJson(errorBodyContent, JourneyResponseDto::class.java)
                            val journey = BusJourneyMapper.mapToDomain(dto)
                            Log.d("API_RESPONSE", "300 response - Disambiguation options: ${journey.requiresDisambiguation()}")
                            emit(journey)
                        } catch (e: Exception) {
                            Log.e("API_RESPONSE", "Failed to parse 300 disambiguation response", e)
                            emit(null)
                        }
                    } else {
                        Log.e("API_RESPONSE", "300 response with no error body")
                        emit(null)
                    }
                }
                else -> {
                    Log.e("API_RESPONSE", "Unexpected response code: ${response.code()}")
                    emit(null)
                }
            }
        } catch (e: Exception) {
            Log.e("API_RESPONSE", "Journey planning failed", e)
            emit(null)
        }
    }

    override suspend fun getBusArrivalsById(lineId: String): Flow<List<BusArrival>> =
        flow {
            try {
                val arrivalResponse = apiService.getBusRouteArrivalsById(lineId)
                if (!arrivalResponse.isSuccessful || arrivalResponse.body() == null) {
                    emit(emptyList())
                    return@flow
                }


                val arrivals = BusArrivalMapper.mapToDomainList(arrivalResponse.body()!!)


                val routeResponse = apiService.getOutboundRouteSequence(lineId)
                if (!routeResponse.isSuccessful || routeResponse.body() == null) {
                    emit(arrivals)
                    return@flow
                }

                val routeSequence = routeResponse.body()!!.stations

                val arrivalsWithCoords = arrivals.map { arrival ->
                    val stop = routeSequence?.find { it.id == arrival.naptanId }
                    if (stop != null) {
                        Log.d(
                            "COORD_MATCH",
                            "Found coords for ${arrival.naptanId}: lat=${stop.lat}, lon=${stop.lon}"
                        )
                        arrival.copy(lat = stop.lat, lon = stop.lon)
                    } else {
                        Log.d("COORD_MATCH", "No coords found for ${arrival.naptanId}")
                        arrival
                    }
                }
                emit(arrivalsWithCoords)
            } catch (_: Exception) {
                emit(emptyList())
            }
        }

    override suspend fun getOutboundRouteSequence(lineId: String): Flow<BusRoute?> = flow {
        try {
            val response = apiService.getOutboundRouteSequence(lineId)
            if (response.isSuccessful) {
                val routeSequence =
                    response.body()?.let { BusRouteMapper.mapToDomain(it) }
                emit(routeSequence)
            } else {
                emit(null)
            }
        } catch (_: Exception) {
            emit(null)
        }
    }
}