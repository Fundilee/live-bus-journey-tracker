package com.livebusjourneytracker.feature.busroutes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BusRoutesViewModel(
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusRoutesUiState())
    val uiState: StateFlow<BusRoutesUiState> = _uiState.asStateFlow()

    fun loadBusRoutes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // TODO: Make API call to load bus routes
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    routes = emptyList()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}

data class BusRoutesUiState(
    val isLoading: Boolean = false,
    val routes: List<String> = emptyList(),
    val error: String? = null
)