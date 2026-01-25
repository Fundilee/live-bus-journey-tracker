package com.livebusjourneytracker.core.domain.usecase

import com.livebusjourneytracker.core.domain.model.ArrivalBusStop
import com.livebusjourneytracker.core.domain.repository.BusRoutesRepository
import kotlinx.coroutines.flow.Flow

class GetBusArrivalsUseCase(
    private val repository: BusRoutesRepository
) {
    suspend operator fun invoke(
        lineId: String
    ): Flow<List<ArrivalBusStop>> {
        require(lineId.isNotBlank()) { "line id must not be blank" }
        
        return repository.getBusRouteArrivalsById(lineId)
    }
}