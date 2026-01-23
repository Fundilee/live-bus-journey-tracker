package com.livebusjourneytracker.feature.busroutes

import com.livebusjourneytracker.feature.busroutes.domain.model.BusRoute
import com.livebusjourneytracker.feature.busroutes.domain.model.BusStop

class BusRouteContract {

    data class BusRoutesUiState(
        val isLoading: Boolean = false,
        val isLoadingStops: Boolean = false,
        val routes: List<BusRoute> = emptyList(),
        val nearbyStops: List<BusStop> = emptyList(),
        val error: String? = null,
        val fromLocation: String = "",
        val toLocation: String = "",
        val selectedToDestination: String = "",
        val selectedFromDestination: String = "",
    )

    sealed class BusRoutesEvent {
        data class UpdateFromLocation(val fromLocation: String) : BusRoutesEvent()
        data class UpdateToLocation(val toLocation: String) : BusRoutesEvent()
        data class UpdateSelectedDestination(val selectedDestination: BusRoute) : BusRoutesEvent()
        object FetchBusRoutes : BusRoutesEvent()
        object FetchNearbyStops : BusRoutesEvent()
    }
}