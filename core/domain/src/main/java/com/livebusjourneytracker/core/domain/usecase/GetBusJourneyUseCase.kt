package com.livebusjourneytracker.core.domain.usecase

import com.livebusjourneytracker.core.domain.model.Journey
import com.livebusjourneytracker.core.domain.repository.BusRoutesRepository
import kotlinx.coroutines.flow.Flow

class GetBusJourneyUseCase(
    private val repository: BusRoutesRepository
) {
    suspend operator fun invoke(from: String, to: String): Flow<Journey?> {
        require(from.isNotBlank()) { "From location cannot be empty" }
        require(to.isNotBlank()) { "To location cannot be empty" }
        
        return repository.planJourney(from, to)
    }
}