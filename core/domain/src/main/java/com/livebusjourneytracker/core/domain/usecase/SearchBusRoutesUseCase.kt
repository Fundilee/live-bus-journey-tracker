package com.livebusjourneytracker.core.domain.usecase

import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.repository.BusRoutesRepository
import kotlinx.coroutines.flow.Flow

class SearchBusRoutesUseCase(
    private val repository: BusRoutesRepository
) {
    suspend operator fun invoke(query: String): Flow<Result<List<BusRoute>>> {
        return repository.searchBusRoutes(query.trim())
    }
}