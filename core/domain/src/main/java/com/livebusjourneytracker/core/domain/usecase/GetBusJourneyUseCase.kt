package com.livebusjourneytracker.core.domain.usecase

import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.model.Journey
import com.livebusjourneytracker.core.domain.repository.BusRoutesRepository
import kotlinx.coroutines.flow.Flow

class GetBusJourneyUseCase(
    private val repository: BusRoutesRepository
) {
    suspend operator fun invoke(lineId: String): Flow<List<BusRoute?>> {
        require(lineId.isNotBlank()) { "line id cannot be empty" }

        return repository.getRouteSequence(lineId)
    }
}