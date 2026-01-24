package com.livebusjourneytracker.feature.tracking

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    modifier: Modifier = Modifier,
    viewModel: TrackingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var trackingInput by remember { mutableStateOf("") }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bus Tracking",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        OutlinedTextField(
            value = trackingInput,
            onValueChange = { trackingInput = it },
            label = { Text("Enter bus route or stop") },
            placeholder = { Text("e.g., Bus 25, Victoria Station") },
            enabled = !uiState.isTracking,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    viewModel.startTracking(trackingInput)
                },
                enabled = !uiState.isTracking && trackingInput.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) {
                if (uiState.isTracking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Start Tracking")
                }
            }
            
            if (uiState.isTracking) {
                Button(
                    onClick = {
                        viewModel.stopTracking()
                        trackingInput = ""
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Stop")
                }
            }
        }
        
        if (uiState.status.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = uiState.status,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}