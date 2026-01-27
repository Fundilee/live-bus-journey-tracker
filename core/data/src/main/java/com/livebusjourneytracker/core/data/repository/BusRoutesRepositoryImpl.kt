package com.livebusjourneytracker.core.data.repository

import com.livebusjourneytracker.core.data.api.TflApiService
import com.livebusjourneytracker.core.data.dto.BusRouteDto
import com.livebusjourneytracker.core.data.dto.JourneyResponseDto
import com.livebusjourneytracker.core.data.mapper.BusArrivalMapper
import com.livebusjourneytracker.core.data.mapper.BusJourneyMapper
import com.livebusjourneytracker.core.data.mapper.BusRouteMapper
import com.livebusjourneytracker.core.data.mapper.SearchMapper
import com.livebusjourneytracker.core.domain.model.BusArrival
import com.livebusjourneytracker.core.domain.model.BusJourney
import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.repository.BusRoutesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class BusRoutesRepositoryImpl(
    private val apiService: TflApiService
) : BusRoutesRepository {

    private val routeSequenceCache = mutableMapOf<String, BusRouteDto>()

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

            when {
                response.isSuccessful -> {
                    val journey = response.body()?.let { BusJourneyMapper.mapToDomain(it) }
                    emit(journey)
                }

                response.code() == 300 -> {
                    val errorBodyContent = response.errorBody()?.string()
                    if (errorBodyContent != null) {
                        try {
                            val dto = com.google.gson.Gson()
                                .fromJson(errorBodyContent, JourneyResponseDto::class.java)
                            val journey = BusJourneyMapper.mapToDomain(dto)
                            emit(journey)
                        } catch (e: Exception) {
                            emit(null)
                        }
                    } else {
                        emit(null)
                    }
                }

                else -> {
                    emit(null)
                }
            }
        } catch (e: Exception) {
            emit(null)
        }
    }

    override suspend fun getBusArrivalsById(lineId: String): Flow<List<BusArrival>> = flow {
        if (!routeSequenceCache.containsKey(lineId)) {
            try {
                val routeResponse = apiService.getOutboundRouteSequence(lineId)
                if (routeResponse.isSuccessful && routeResponse.body() != null) {
                    routeSequenceCache[lineId] = routeResponse.body()!!
                }
            } catch (_: Exception) {
                // Cache will remain empty for this lineId
            }
        }

        while (true) {
            try {
                val arrivalResponse = apiService.getBusRouteArrivalsById(lineId)
                if (arrivalResponse.isSuccessful && arrivalResponse.body() != null) {
                    val arrivals = BusArrivalMapper.mapToDomainList(arrivalResponse.body()!!)

                    val enrichedArrivals = routeSequenceCache[lineId]?.let { cachedRoute ->
                        arrivals.map { arrival ->
                            val stop = cachedRoute.stations?.find { it.id == arrival.naptanId }
                            if (stop != null) {
                                arrival.copy(
                                    lat = stop.lat,
                                    lon = stop.lon,
                                    lines = cachedRoute.lineStrings ?: emptyList()
                                )
                            } else {
                                arrival
                            }
                        }
                    } ?: arrivals

                    emit(enrichedArrivals)
                } else {
                    emit(emptyList())
                }
            } catch (_: Exception) {
                emit(emptyList())
            }
            delay(30_000)
        }
    }
}