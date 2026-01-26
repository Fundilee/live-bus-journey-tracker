package com.livebusjourneytracker.feature.busroutes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livebusjourneytracker.core.domain.model.BusJourney
import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.model.DisambiguationOption
import com.livebusjourneytracker.core.domain.model.DisambiguationType
import com.livebusjourneytracker.core.domain.model.getDisambiguationNeeded
import com.livebusjourneytracker.core.domain.model.requiresDisambiguation
import com.livebusjourneytracker.core.domain.usecase.GetBusArrivalsUseCase
import com.livebusjourneytracker.core.domain.usecase.GetJourneyResultsUseCase
import com.livebusjourneytracker.core.domain.usecase.GetOutboundRouteSequenceUseCase
import com.livebusjourneytracker.core.domain.usecase.SearchBusRoutesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BusRoutesViewModel(
    private val getBusArrivalsUseCase: GetBusArrivalsUseCase,
    private val searchBusRoutesUseCase: SearchBusRoutesUseCase,
    private val getOutboundRouteSequenceUseCase: GetOutboundRouteSequenceUseCase,
    private val getJourneyResultsUseCase: GetJourneyResultsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusRouteContract.BusRoutesUiState())
    val uiState: StateFlow<BusRouteContract.BusRoutesUiState> = _uiState.asStateFlow()

    fun searchRoutes(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                searchBusRoutesUseCase(query).collect { routes ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            routes = routes
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun fetchLiveBuses(lineId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                getBusArrivalsUseCase(lineId).collect { busArrivals ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isBusArrivalSuccess = true,
                            busArrivals = busArrivals
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isBusArrivalSuccess = false,
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun getOutboundRouteSequence(lineId: String) {
        //TODO Refresh every 30seconds
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                getOutboundRouteSequenceUseCase(lineId).collect { busRouteSequence ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            busRouteSequence = busRouteSequence
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    private fun planJourney() {
        val from = uiState.value.fromLocation
        val to = uiState.value.toLocation
        if (from.isBlank() || to.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingJourney = true, requiresDisambiguation = false) }
            try {
                getJourneyResultsUseCase.invoke(from = from, to = to)
                    .collect { journey ->
                        handleJourneyResponse(journey)
                    }
                println("Journey planned from '$from' to '$to'")
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingJourney = false,
                        error = "Failed to plan journey: ${e.message}"
                    )
                }
            }
        }
    }

//    private fun matchNapTanId(): List<BusArrival> {
//        val arrivals: List<BusArrival>? = uiState.value.busArrivals
//        val busRouteSequence = uiState.value.busRouteSequence
//        val arrivalsWithCoords = arrivals?.map { arrival ->
//            val stop = busRouteSequence?.stations?.find { it.id == arrival.naptanId }
//            if (stop != null) {
//                arrival.copy(lat = stop.lat, lon = stop.lon)
//            } else {
//                arrival
//            }
//        } ?: emptyList()
//        return arrivalsWithCoords
//    }

    private fun handleJourneyResponse(journey: BusJourney?) {
//        if (journey == null) {
//            _uiState.update {
//                it.copy(
//                    isLoadingJourney = false,
//                    error = "No journey found"
//                )
//            }
//            return
//        }

        if (journey?.requiresDisambiguation()==true) {
            // Handle disambiguation
            val disambiguationTypes = journey.getDisambiguationNeeded()
            _uiState.update {
                it.copy(
                    isLoadingJourney = false,
                    journey = journey,
                    requiresDisambiguation = true,
                    disambiguationTypes = disambiguationTypes,
                    fromDisambiguationOptions = journey.fromLocationDisambiguation?.disambiguationOptions
                        ?: emptyList(),
                    toDisambiguationOptions = journey.toLocationDisambiguation?.disambiguationOptions
                        ?: emptyList(),
                    viaDisambiguationOptions = journey.viaLocationDisambiguation?.disambiguationOptions
                        ?: emptyList()
                )
            }
        } else {
            // Valid journey result
            _uiState.update {
                it.copy(
                    isLoadingJourney = false,
                    journey = journey,
                    requiresDisambiguation = false,
                    journeyPlanned = true
                )
            }
        }
    }


    fun clearError() {
        _uiState.update {
            it.copy(error = null)
        }
    }

    fun clearJourney() {
        _uiState.update {
            it.copy(
                journey = null,
                requiresDisambiguation = false,
                journeyPlanned = false
            )
        }
    }

    fun setEvents(event: BusRouteContract.BusRoutesEvent) {
        when (event) {
            is BusRouteContract.BusRoutesEvent.UpdateFromLocation -> {
                _uiState.update { it.copy(fromLocation = event.fromLocation) }
            }

            is BusRouteContract.BusRoutesEvent.UpdateToLocation -> {
                _uiState.update { it.copy(toLocation = event.toLocation) }
            }

            is BusRouteContract.BusRoutesEvent.UpdateSelectedDestination -> {
                checkAndCallJourneyPlanningApi(event.selectedDestination)
            }

            is BusRouteContract.BusRoutesEvent.SelectDisambiguationOption -> {
                handleDisambiguationSelection(event.type, event.option)
            }

            is BusRouteContract.BusRoutesEvent.RetryJourneyWithSelectedOptions -> {
                retryJourneyWithSelectedOptions()
            }

            is BusRouteContract.BusRoutesEvent.ClearDisambiguation -> {
                clearDisambiguation()
            }

            is BusRouteContract.BusRoutesEvent.FetchLiveBuses -> {
                fetchLiveBuses(event.lineId)

            }
        }
    }

    private fun checkAndCallJourneyPlanningApi(selectedDestination: BusRoute) {
        _uiState.update {
            when (it.activeField) {
                BusRouteContract.ActiveSearchField.FROM -> it.copy(
                    fromLocation = selectedDestination.name,
                    activeField = BusRouteContract.ActiveSearchField.TO
                )

                BusRouteContract.ActiveSearchField.TO -> it.copy(
                    toLocation = selectedDestination.name,
                    activeField = BusRouteContract.ActiveSearchField.FROM
                )

                else -> uiState
            } as BusRouteContract.BusRoutesUiState
        }
        planJourney()
    }

    private fun handleDisambiguationSelection(
        type: DisambiguationType,
        option: DisambiguationOption
    ) {
        _uiState.update { currentState ->
            when (type) {
                DisambiguationType.FROM -> currentState.copy(selectedFromOption = option)
                DisambiguationType.TO -> currentState.copy(selectedToOption = option)
                DisambiguationType.VIA -> currentState.copy(selectedViaOption = option)
            }
        }
    }

    private fun retryJourneyWithSelectedOptions() {
        val currentState = _uiState.value

        // Use resolved locations for the new journey request
        val fromLocation =
            currentState.selectedFromOption?.parameterValue ?: currentState.fromLocation
        val toLocation =
            currentState.selectedToOption?.parameterValue ?: currentState.toLocation

        _uiState.update {
            it.copy(
                fromLocation = fromLocation,
                toLocation = toLocation,
                requiresDisambiguation = false,
                selectedFromOption = null,
                selectedToOption = null,
                selectedViaOption = null
            )
        }

        planJourney()
    }

    private fun clearDisambiguation() {
        _uiState.update {
            it.copy(
                requiresDisambiguation = false,
                disambiguationTypes = emptyList(),
                fromDisambiguationOptions = emptyList(),
                toDisambiguationOptions = emptyList(),
                viaDisambiguationOptions = emptyList(),
                selectedFromOption = null,
                selectedToOption = null,
                selectedViaOption = null
            )
        }
    }

    fun updateFieldFocus(activeField: BusRouteContract.ActiveSearchField) {
        _uiState.update {
            it.copy(
                activeField = activeField
            )
        }
    }
}