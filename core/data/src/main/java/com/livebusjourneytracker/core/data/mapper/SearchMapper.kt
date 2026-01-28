package com.livebusjourneytracker.core.data.mapper

import com.livebusjourneytracker.core.network.dto.MatchedStopDto
import com.livebusjourneytracker.core.domain.model.BusRoute

object SearchMapper {
    
    fun mapMatchedStopToBusRoute(matchedStop: MatchedStopDto): BusRoute {
        return BusRoute(
            id = matchedStop.id,
            name = matchedStop.name,
            lat = matchedStop.lat,
            lon = matchedStop.lon,
            lineStatuses = emptyList(),
            routeSections = emptyList(),
            serviceTypes = emptyList()
        )
    }
    
    fun mapMatchedStopsToBusRoutes(matchedStops: List<MatchedStopDto>): List<BusRoute> {
        return matchedStops
            .filter { "bus" in it.modes } // Only include stops that have bus service
            .map { mapMatchedStopToBusRoute(it) }
            .distinctBy { it.id } // Remove duplicates
    }
}