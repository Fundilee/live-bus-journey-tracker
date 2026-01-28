package com.livebusjourneytracker.core.domain.repository

import com.livebusjourneytracker.core.domain.model.BusArrival
import com.livebusjourneytracker.core.domain.model.BusJourney
import com.livebusjourneytracker.core.domain.model.BusRoute
import kotlinx.coroutines.flow.Flow

interface BusRoutesRepository {

    suspend fun searchBusRoutes(query: String): Flow<Result<List<BusRoute>>>

    suspend fun planJourney(from: String, to: String): Flow<Result<BusJourney?>>

    suspend fun getBusArrivalsById(lineId: String): Flow<Result<List<BusArrival>>>
}