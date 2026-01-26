package com.livebusjourneytracker.core.data.repository

import android.util.Log
import com.livebusjourneytracker.core.data.api.TflApiService
import com.livebusjourneytracker.core.data.mapper.BusArrivalMapper
import com.livebusjourneytracker.core.data.mapper.BusJourneyMapper
import com.livebusjourneytracker.core.data.mapper.BusRouteMapper
import com.livebusjourneytracker.core.data.mapper.SearchMapper
import com.livebusjourneytracker.core.domain.model.BusArrival
import com.livebusjourneytracker.core.domain.model.BusJourney
import com.livebusjourneytracker.core.domain.model.BusRoute
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
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun planJourney(from: String, to: String): Flow<BusJourney?> = flow {
        try {
            val response = apiService.journeyResults(from, to)
            if (response.isSuccessful) {
                Log.i(
                    "Journey Response is as follows", response.body()?.journeys
                        .toString()
                )
                try {
                    val journey = response.body()?.let { BusJourneyMapper.mapToDomain(it) }

                    Log.e("Journey Response is as follows 2", journey.toString())
                    emit(journey)

                } catch (e: Exception) {
                    Log.e("Journey MAPPING FAILED", e.message ?: "unknown", e)
                }
            } else {
                emit(null)
            }
        } catch (e: Exception) {
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
                        arrival.copy(lat = stop.lat, lon = stop.lon)
                    } else {
                        arrival
                    }
                }
                emit(arrivalsWithCoords)
            } catch (e: Exception) {
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
        } catch (e: Exception) {
            emit(null)
        }
    }
}