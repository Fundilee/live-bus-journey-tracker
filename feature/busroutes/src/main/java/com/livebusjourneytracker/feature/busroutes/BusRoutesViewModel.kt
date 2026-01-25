package com.livebusjourneytracker.feature.busroutes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livebusjourneytracker.core.domain.model.BusJourney
import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.model.DisambiguationOption
import com.livebusjourneytracker.core.domain.model.DisambiguationType
import com.livebusjourneytracker.core.domain.model.getDisambiguationNeeded
import com.livebusjourneytracker.core.domain.model.requiresDisambiguation
import com.livebusjourneytracker.core.domain.usecase.GetJourneyResultsUseCase
import com.livebusjourneytracker.core.domain.usecase.GetBusArrivalsUseCase
import com.livebusjourneytracker.core.domain.usecase.SearchBusRoutesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BusRoutesViewModel(
    private val getBusArrivalsUseCase: GetBusArrivalsUseCase,
    private val searchBusRoutesUseCase: SearchBusRoutesUseCase,
    private val getNearbyStopsUseCase: GetBusArrivalsUseCase,
    private val getJourneyResultsUseCase: GetJourneyResultsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusRouteContract.BusRoutesUiState())
    val uiState: StateFlow<BusRouteContract.BusRoutesUiState> = _uiState.asStateFlow()


    fun searchRoutes(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
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

    private fun handleJourneyResponse(journey: BusJourney?) {
        if (journey == null) {
            _uiState.update {
                it.copy(
                    isLoadingJourney = false,
                    error = "No journey found"
                )
            }
            return
        }

        if (journey.requiresDisambiguation()) {
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

            else -> Unit
        }
    }

    private fun checkAndCallJourneyPlanningApi(selectedDestination: BusRoute) {
        _uiState.update {
            when (it.activeField) {
                BusRouteContract.ActiveSearchField.FROM -> it.copy(
                    fromLocation = selectedDestination.lat.toString() + "," + selectedDestination.lon.toString(),
                    activeField = BusRouteContract.ActiveSearchField.TO
                )

                BusRouteContract.ActiveSearchField.TO -> it.copy(
                    toLocation = selectedDestination.lat.toString() + "," + selectedDestination.lon.toString(),
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
        val toLocation = currentState.selectedToOption?.parameterValue ?: currentState.toLocation

        // Clear disambiguation state before making new request
        _uiState.update {
            it.copy(
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