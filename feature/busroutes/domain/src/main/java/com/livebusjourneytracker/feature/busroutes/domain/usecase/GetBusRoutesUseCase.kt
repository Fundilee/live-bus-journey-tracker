package com.livebusjourneytracker.feature.busroutes.domain.usecase

import com.livebusjourneytracker.feature.busroutes.domain.model.BusRoute
import com.livebusjourneytracker.feature.busroutes.domain.repository.BusRoutesRepository
import kotlinx.coroutines.flow.Flow

class GetBusRoutesUseCase(
    private val repository: BusRoutesRepository
) {
    suspend operator fun invoke(): Flow<List<BusRoute>> {
        return repository.getAllBusRoutes()
    }
}