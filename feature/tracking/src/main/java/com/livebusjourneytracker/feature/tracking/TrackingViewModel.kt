package com.livebusjourneytracker.feature.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrackingViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(TrackingUiState())
    val uiState: StateFlow<TrackingUiState> = _uiState.asStateFlow()
    
    fun startTracking(input: String) {
        if (input.isBlank()) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isTracking = true,
                trackingInput = input,
                status = "Tracking $input..."
            )
            
            // TODO: Implement actual tracking logic
            kotlinx.coroutines.delay(2000)
            
            _uiState.value = _uiState.value.copy(
                status = "Successfully tracking: $input"
            )
        }
    }
    
    fun stopTracking() {
        _uiState.value = _uiState.value.copy(
            isTracking = false,
            trackingInput = "",
            status = "Tracking stopped"
        )
    }
    
    fun clearStatus() {
        _uiState.value = _uiState.value.copy(status = "")
    }
}

data class TrackingUiState(
    val isTracking: Boolean = false,
    val trackingInput: String = "",
    val status: String = ""
)