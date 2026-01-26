package com.livebusjourneytracker.feature.busroutes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livebusjourneytracker.core.domain.model.BusJourney
import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.model.DisambiguationOption
import com.livebusjourneytracker.core.domain.model.DisambiguationType
import com.livebusjourneytracker.core.domain.model.Journeys
import com.livebusjourneytracker.core.domain.model.getDisambiguationNeeded
import com.livebusjourneytracker.core.domain.model.requiresDisambiguation
import com.livebusjourneytracker.core.domain.usecase.GetBusArrivalsUseCase
import com.livebusjourneytracker.core.domain.usecase.GetJourneyResultsUseCase
import com.livebusjourneytracker.core.domain.usecase.SearchBusRoutesUseCase
import com.livebusjourneytracker.feature.busroutes.ui.BusRouteContract
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray

class BusRoutesViewModel(
    private val getBusArrivalsUseCase: GetBusArrivalsUseCase,
    private val searchBusRoutesUseCase: SearchBusRoutesUseCase,
    private val getJourneyResultsUseCase: GetJourneyResultsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusRouteContract.BusRoutesUiState())
    val uiState: StateFlow<BusRouteContract.BusRoutesUiState> = _uiState.asStateFlow()

    private var busTrackingJob: Job? = null

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
        busTrackingJob?.cancel()

        // Store the lineId for resuming after background
        _uiState.update { it.copy(currentTrackingLineId = lineId) }

        busTrackingJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                getBusArrivalsUseCase(lineId).collect { busArrivals ->
                    val lines: List<List<BusRouteContract.BusCoordinates>> =
                        busArrivals.flatMap { it.lines }
                            .map { line ->
                                parseLineString(line)
                            }
                            .filter { it.isNotEmpty() }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isBusArrivalSuccess = true,
                            busArrivals = busArrivals,
                            lines = lines
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

    fun resumeBusTracking() {
        val lineId = uiState.value.currentTrackingLineId
        if (lineId != null) {
            fetchLiveBuses(lineId)
        }
    }

    fun stopBusTracking() {
        busTrackingJob?.cancel()
        busTrackingJob = null
        // Don't clear cache here - only pause tracking, cache preserved for resume
    }

    private fun planJourney() {
        val from = uiState.value.fromLocation
        val to = uiState.value.toLocation
        if (from.isBlank() || to.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingJourney = true,) }
            try {
                getJourneyResultsUseCase.invoke(from = from, to = to)
                    .collect { journey ->
                        handleJourneyResponse(journey)
                    }
                println("Journey planned from '$from' to '$to'")
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Failed to plan journey: ${e.message}",
                    )
                }
            }
        }
    }

    private fun handleJourneyResponse(journey: BusJourney?) {
        if (journey == null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "No journey found",
                )
            }
            return
        }

        if (journey.requiresDisambiguation()) {
            val disambiguationTypes = journey.getDisambiguationNeeded()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    journey = journey,
                    requiresDisambiguation = true,
                    disambiguationTypes = disambiguationTypes,
                    fromDisambiguationOptions = journey.fromLocationDisambiguation?.disambiguationOptions
                        ?: emptyList(),
                    toDisambiguationOptions = journey.toLocationDisambiguation?.disambiguationOptions
                        ?: emptyList(),
                    viaDisambiguationOptions = journey.viaLocationDisambiguation?.disambiguationOptions
                        ?: emptyList(),
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    journey = journey,
                    journeyPlanned = true,
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
                journeyPlanned = false,
                disambiguationTypes = emptyList(),
                fromDisambiguationOptions = emptyList(),
                toDisambiguationOptions = emptyList(),
                viaDisambiguationOptions = emptyList(),
                selectedFromOption = null,
                selectedToOption = null,
                selectedViaOption = null,
                busArrivals = null,
                lines = null,
                isBusArrivalSuccess = false,
                currentTrackingLineId = null
            )
        }
    }
    
    fun resetToInitialState() {
        // Stop any active tracking
        busTrackingJob?.cancel()
        busTrackingJob = null
        
        // Reset UI state to initial values
        _uiState.update {
            BusRouteContract.BusRoutesUiState() // Reset to default empty state
        }
        clearJourney()
        clearDisambiguation()
    }

    fun setEvents(event: BusRouteContract.BusRoutesEvent) {
        when (event) {
            is BusRouteContract.BusRoutesEvent.UpdateFromLocation -> {
                _uiState.update { it.copy(fromLocation = event.fromLocation,) }
            }

            is BusRouteContract.BusRoutesEvent.UpdateToLocation -> {
                _uiState.update { it.copy(toLocation = event.toLocation,) }
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
                val busLeg = event.journey.legs.firstOrNull()
                val arrivalPoint = BusRouteContract.BusCoordinates(
                    lat = busLeg?.arrivalPoint?.lat,
                    lon = busLeg?.arrivalPoint?.lon
                )
                val departurePoint = BusRouteContract.BusCoordinates(
                    lat = busLeg?.departurePoint?.lat,
                    lon = busLeg?.departurePoint?.lon
                )
                _uiState.update {
                    it.copy(
                        fromCoordinates = departurePoint,
                        toCoordinates = arrivalPoint,
                    )
                }

                val lineId = getBusLineId(event.journey)
                lineId?.let { fetchLiveBuses(it) }

            }
        }
    }

    private fun getBusLineId(journey: Journeys): String? {
        return journey.legs
            .firstOrNull { it.mode?.id == "bus" }
            ?.routeOptions
            ?.firstOrNull()
            ?.name
    }

    private fun checkAndCallJourneyPlanningApi(selectedDestination: BusRoute) {
        _uiState.update {
            when (it.activeField) {
                BusRouteContract.ActiveSearchField.FROM -> it.copy(
                    fromLocation = selectedDestination.name,
                    activeField = BusRouteContract.ActiveSearchField.TO,
                )

                BusRouteContract.ActiveSearchField.TO -> it.copy(
                    toLocation = selectedDestination.name,
                    activeField = BusRouteContract.ActiveSearchField.FROM,
                )

                else -> it
            }
        }
        planJourney()
    }

    private fun handleDisambiguationSelection(
        type: DisambiguationType,
        option: DisambiguationOption
    ) {
        _uiState.update { currentState ->
            when (type) {
                DisambiguationType.FROM -> currentState.copy(selectedFromOption = option,)
                DisambiguationType.TO -> currentState.copy(selectedToOption = option,)
                DisambiguationType.VIA -> currentState.copy(selectedViaOption = option,)
            }
        }
    }

    private fun retryJourneyWithSelectedOptions() {
        val currentState = _uiState.value

        val fromLocation =
            currentState.selectedFromOption?.parameterValue ?: currentState.fromLocation
        val toLocation =
            currentState.selectedToOption?.parameterValue ?: currentState.toLocation

        _uiState.update {
            it.copy(
                fromLocation = fromLocation,
                toLocation = toLocation,
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
                activeField = activeField,
            )
        }
    }

    fun parseLineString(lineString: String): List<BusRouteContract.BusCoordinates> {
        val outerArray = JSONArray(lineString)
        val linesArray = outerArray.getJSONArray(0)
        val length = linesArray.length()

        return List(length) { i ->
            val point = linesArray.getJSONArray(i)
            BusRouteContract.BusCoordinates(
                lon = point.getDouble(0),
                lat = point.getDouble(1)
            )
        }
    }
}