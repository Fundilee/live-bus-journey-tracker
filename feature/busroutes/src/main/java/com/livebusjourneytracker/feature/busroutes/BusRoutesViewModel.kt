package com.livebusjourneytracker.feature.busroutes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livebusjourneytracker.feature.busroutes.domain.usecase.GetBusRoutesUseCase
import com.livebusjourneytracker.feature.busroutes.domain.usecase.GetNearbyStopsUseCase
import com.livebusjourneytracker.feature.busroutes.domain.usecase.SearchBusRoutesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BusRoutesViewModel(
    private val getBusRoutesUseCase: GetBusRoutesUseCase,
    private val searchBusRoutesUseCase: SearchBusRoutesUseCase,
    private val getNearbyStopsUseCase: GetNearbyStopsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusRouteContract.BusRoutesUiState())
    val uiState: StateFlow<BusRouteContract.BusRoutesUiState> = _uiState.asStateFlow()

    fun loadBusRoutes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                getBusRoutesUseCase().collect { routes ->
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

    fun searchRoutes(query: String) {
        if (query.isBlank()) {
            loadBusRoutes()
            return
        }

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

    fun loadNearbyStops(latitude: Double, longitude: Double, radius: Int = 500) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingStops = true)
            try {
                getNearbyStopsUseCase(latitude, longitude, radius).collect { stops ->
                    _uiState.update {
                        it.copy(
                            isLoadingStops = false,
                            nearbyStops = stops
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingStops = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(error = null)
        }
    }

    fun setEvents(updateToLocation: BusRouteContract.BusRoutesEvent) {
        when (updateToLocation) {
            is BusRouteContract.BusRoutesEvent.UpdateFromLocation -> {
                _uiState.update { it.copy(fromLocation = updateToLocation.fromLocation) }
            }

            is BusRouteContract.BusRoutesEvent.UpdateToLocation -> {
                _uiState.update { it.copy(toLocation = updateToLocation.toLocation) }
            }

            is BusRouteContract.BusRoutesEvent.UpdateSelectedDestination -> {
                if (_uiState.value.fromLocation.isNotBlank() && _uiState.value.toLocation.isNotBlank()) {
            }
        }
            else -> Unit
        }
    }
}