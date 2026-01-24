package com.livebusjourneytracker.core.domain.usecase

import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.repository.BusRoutesRepository
import kotlinx.coroutines.flow.Flow

class GetBusRoutesUseCase(
    private val repository: BusRoutesRepository
) {
    suspend operator fun invoke(): Flow<List<BusRoute>> {
        return repository.getAllBusRoutes()
    }
}