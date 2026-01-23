package com.livebusjourneytracker.feature.busroutes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.livebusjourneytracker.core.ui.theme.components.SearchView
import com.livebusjourneytracker.feature.busroutes.domain.model.BusRoute
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    viewModel: BusRoutesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var isMapView by remember { mutableStateOf(false) }
    var selectedRoute by remember { mutableStateOf<BusRoute?>(null) }
    
    LaunchedEffect(Unit) {
        viewModel.loadBusRoutes()
        // Load some sample nearby stops for demonstration
        viewModel.loadNearbyStops(51.5074, -0.1278) // London center
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bus Routes",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Row {
                IconButton(
                    onClick = { isMapView = false }
                ) {
                    Icon(
                        Icons.Default.List, 
                        contentDescription = "List View",
                        tint = if (!isMapView) MaterialTheme.colorScheme.primary 
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = { isMapView = true }
                ) {
                    Icon(
                        Icons.Default.LocationOn, 
                        contentDescription = "Map View",
                        tint = if (isMapView) MaterialTheme.colorScheme.primary 
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        SearchView(promptText = "From") {
            viewModel.setEvents(BusRouteContract.BusRoutesEvent.UpdateFromLocation(it))
            viewModel.searchRoutes(it) }

        Spacer(modifier = Modifier.height(16.dp))

        SearchView(promptText = "To") {
            viewModel.setEvents(BusRouteContract.BusRoutesEvent.UpdateToLocation(it))

            viewModel.searchRoutes(it) }

        
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.error != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.clearError() },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
            
            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (isMapView) {
                        BusRoutesMapView(
                            routes = uiState.routes,
                            stops = uiState.nearbyStops,
                            modifier = Modifier.fillMaxSize(),
                            onRouteSelected = { route ->
                                selectedRoute = route
                            },
                            onStopSelected = { stop ->
                                // Handle stop selection if needed
                            }
                        )
                        
                        // Show selected route info card
                        selectedRoute?.let { route ->
                            BusRouteInfoCard(
                                route = route,
                                modifier = Modifier.align(Alignment.BottomCenter),
                                onDismiss = { selectedRoute = null }
                            )
                        }
                    } else {
                        LazyColumn {
                            items(uiState.routes) { route ->
                                BusRouteItem(
                                    route = route,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { viewModel.setEvents(BusRouteContract.BusRoutesEvent.UpdateSelectedDestination(route))}
                                )
                            }
                            
                            if (uiState.routes.isEmpty()) {
                                item {
                                    Text(
                                        text = if (searchQuery.isNotBlank()) "No routes found for '$searchQuery'" else "No bus routes available",
                                        modifier = Modifier.padding(16.dp),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BusRouteItem(
    route: BusRoute,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = route.name,
                style = MaterialTheme.typography.titleMedium
            )
            
            if (route.lineStatuses.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                route.lineStatuses.forEach { status ->
                    Text(
                        text = "Status: ${status.statusSeverityDescription}",
                        style = MaterialTheme.typography.bodySmall,
                        color = when (status.statusSeverity) {
                            10 -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        }
    }
}