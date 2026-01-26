package com.livebusjourneytracker.core.domain.usecase

import com.livebusjourneytracker.core.domain.model.BusArrival
import com.livebusjourneytracker.core.domain.repository.BusRoutesRepository
import kotlinx.coroutines.flow.Flow

class GetBusArrivalsUseCase(
    private val repository: BusRoutesRepository
) {
    suspend operator fun invoke(
        lineId: String
    ): Flow<List<BusArrival>> {
        require(lineId.isNotEmpty()) { "line id must not be empty" }
        
        return repository.getBusArrivalsById(lineId)
    }
}