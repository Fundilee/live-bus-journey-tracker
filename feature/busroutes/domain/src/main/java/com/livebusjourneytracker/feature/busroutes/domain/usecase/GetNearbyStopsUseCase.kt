package com.livebusjourneytracker.feature.busroutes.domain.usecase

import com.livebusjourneytracker.feature.busroutes.domain.model.BusStop
import com.livebusjourneytracker.feature.busroutes.domain.repository.BusRoutesRepository
import kotlinx.coroutines.flow.Flow

class GetNearbyStopsUseCase(
    private val repository: BusRoutesRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int = 500
    ): Flow<List<BusStop>> {
        require(radiusMeters > 0) { "Radius must be positive" }
        require(latitude in -90.0..90.0) { "Latitude must be between -90 and 90" }
        require(longitude in -180.0..180.0) { "Longitude must be between -180 and 180" }
        
        return repository.getNearbyBusStops(latitude, longitude, radiusMeters)
    }
}