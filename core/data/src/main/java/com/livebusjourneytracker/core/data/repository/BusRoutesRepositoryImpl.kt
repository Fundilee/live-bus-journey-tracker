package com.livebusjourneytracker.core.data.repository

import com.livebusjourneytracker.core.network.TflApiService
import com.livebusjourneytracker.core.network.dto.BusRouteDto
import com.livebusjourneytracker.core.network.dto.JourneyResponseDto
import com.livebusjourneytracker.core.data.mapper.BusArrivalMapper
import com.livebusjourneytracker.core.data.mapper.BusJourneyMapper
import com.livebusjourneytracker.core.data.mapper.SearchMapper
import com.livebusjourneytracker.core.domain.model.BusArrival
import com.livebusjourneytracker.core.domain.model.BusJourney
import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.repository.BusRoutesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BusRoutesRepositoryImpl(
    private val apiService: TflApiService
) : BusRoutesRepository {

    private val routeSequenceCache = mutableMapOf<String, BusRouteDto>()

    override suspend fun searchBusRoutes(query: String): Flow<Result<List<BusRoute>>> = flow {
        try {
            val response = apiService.searchBusRoutes(query)
            if (response.isSuccessful) {
                val searchResponse = response.body()
                val routes =
                    searchResponse?.matches?.let { SearchMapper.mapMatchedStopsToBusRoutes(it) }
                        ?: emptyList()
                emit(Result.success(routes))
            } else {
                emit(Result.failure(handleApiErrorCode(response.code())))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun planJourney(from: String, to: String): Flow<Result<BusJourney?>> = flow {
        try {
            val response = apiService.journeyResults(from, to)

            when {
                response.isSuccessful -> {
                    val journey = response.body()?.let { BusJourneyMapper.mapToDomain(it) }
                    emit(Result.success(journey))
                }

                else -> {
                    when {
                        response.code() == 300 -> {
                            val errorBodyContent = response.errorBody()?.string()
                            if (errorBodyContent != null) {
                                try {
                                    val dto = com.google.gson.Gson()
                                        .fromJson(errorBodyContent, JourneyResponseDto::class.java)
                                    val journey = BusJourneyMapper.mapToDomain(dto)
                                    emit(Result.success(journey))
                                } catch (e: Exception) {
                                    emit(Result.failure(Exception(e)))
                                }
                            } else {
                                emit(Result.failure(Exception("Error body is empty")))
                            }
                        }

                        else -> {
                            emit(Result.failure(handleApiErrorCode(response.code())))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun getBusArrivalsById(lineId: String): Flow<Result<List<BusArrival>>> = flow {
        if (!routeSequenceCache.containsKey(lineId)) {
            try {
                val routeResponse = apiService.getOutboundRouteSequence(lineId)
                if (routeResponse.isSuccessful && routeResponse.body() != null) {
                    routeSequenceCache[lineId] = routeResponse.body()!!
                }
            } catch (e: Exception) {
                emit(Result.failure(Exception(e)))
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

                    emit(Result.success(enrichedArrivals))
                } else {
                    emit(Result.failure(handleApiErrorCode(arrivalResponse.code())))
                }
            } catch (e: Exception) {
                emit(Result.failure(java.lang.Exception(e)))
            }
            delay(30_000)
        }
    }

    private fun handleApiErrorCode(code: Int): Exception {
        val exception = if (code == 404) {
            Exception("Requested data not found")
        } else if (code == 429) {
            Exception("Too many requests. Please try again later.")
        } else if (code >= 500) {
            Exception("Server error occurred. Please try again later.")
        } else {
            Exception("Request failed with code")
        }
        return exception
    }
}