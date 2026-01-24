package com.livebusjourneytracker.feature.busroutes

import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.model.BusStop
import com.livebusjourneytracker.core.domain.model.Journey
import com.livebusjourneytracker.core.domain.model.DisambiguationOption
import com.livebusjourneytracker.core.domain.model.DisambiguationType

class BusRouteContract {

    data class BusRoutesUiState(
        val isLoading: Boolean = false,
        val isLoadingStops: Boolean = false,
        val isLoadingJourney: Boolean = false,
        val routes: List<BusRoute> = emptyList(),
        val nearbyStops: List<BusStop> = emptyList(),
        val journey: Journey? = null,
        val error: String? = null,
        val fromLocation: String = "",
        val toLocation: String = "",
        val selectedToDestination: String = "",
        val selectedFromDestination: String = "",
        val journeyPlanned: Boolean = false,
        // Disambiguation state
        val requiresDisambiguation: Boolean = false,
        val disambiguationTypes: List<DisambiguationType> = emptyList(),
        val fromDisambiguationOptions: List<DisambiguationOption> = emptyList(),
        val toDisambiguationOptions: List<DisambiguationOption> = emptyList(),
        val viaDisambiguationOptions: List<DisambiguationOption> = emptyList(),
        val selectedFromOption: DisambiguationOption? = null,
        val selectedToOption: DisambiguationOption? = null,
        val selectedViaOption: DisambiguationOption? = null,
    )

    sealed class BusRoutesEvent {
        data class UpdateFromLocation(val fromLocation: String) : BusRoutesEvent()
        data class UpdateToLocation(val toLocation: String) : BusRoutesEvent()
        data class UpdateSelectedDestination(val selectedDestination: BusRoute) : BusRoutesEvent()
        object FetchBusRoutes : BusRoutesEvent()
        object FetchNearbyStops : BusRoutesEvent()
        
        // Disambiguation events
        data class SelectDisambiguationOption(
            val type: DisambiguationType,
            val option: DisambiguationOption
        ) : BusRoutesEvent()
        object RetryJourneyWithSelectedOptions : BusRoutesEvent()
        object ClearDisambiguation : BusRoutesEvent()
    }
}