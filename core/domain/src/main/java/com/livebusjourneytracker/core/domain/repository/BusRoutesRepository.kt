package com.livebusjourneytracker.core.domain.repository

import com.livebusjourneytracker.core.domain.model.ArrivalBusStop
import com.livebusjourneytracker.core.domain.model.BusJourney
import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.model.BusStop
import com.livebusjourneytracker.core.domain.model.Journey
import kotlinx.coroutines.flow.Flow

interface BusRoutesRepository {

    suspend fun searchBusRoutes(query: String): Flow<List<BusRoute>>

    suspend fun getAllBusJourneys(from: String, to: String): Flow<List<BusRoute>>
    
    suspend fun planJourney(from: String, to: String): Flow<BusJourney?>

    suspend fun getBusRouteArrivalsById(lineId: String): Flow<List<ArrivalBusStop>>

    suspend fun getRouteSequence(lineId: String): Flow<List<BusRoute>>


}