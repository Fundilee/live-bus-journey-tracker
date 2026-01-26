package com.livebusjourneytracker.core.domain.usecase

import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.repository.BusRoutesRepository
import kotlinx.coroutines.flow.Flow

class GetOutboundRouteSequenceUseCase(
    private val repository: BusRoutesRepository
) {
    suspend operator fun invoke(lineId: String): Flow<BusRoute?> {
        require(lineId.isNotBlank()) { "line id cannot be empty" }

        return repository.getOutboundRouteSequence(lineId)
    }
}