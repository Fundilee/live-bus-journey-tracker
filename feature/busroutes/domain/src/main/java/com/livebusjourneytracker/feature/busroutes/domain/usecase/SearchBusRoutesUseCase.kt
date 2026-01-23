package com.livebusjourneytracker.feature.busroutes.domain.usecase

import com.livebusjourneytracker.feature.busroutes.domain.model.BusRoute
import com.livebusjourneytracker.feature.busroutes.domain.repository.BusRoutesRepository
import kotlinx.coroutines.flow.Flow

class SearchBusRoutesUseCase(
    private val repository: BusRoutesRepository
) {
    suspend operator fun invoke(query: String): Flow<List<BusRoute>> {
        return if (query.isBlank()) {
            repository.getAllBusRoutes()
        } else {
            repository.searchBusRoutes(query.trim())
        }
    }
}