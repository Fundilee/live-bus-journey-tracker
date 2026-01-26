package com.livebusjourneytracker.feature.busroutes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.ui.theme.components.BottomSheetView
import com.livebusjourneytracker.core.ui.theme.components.BusRouteItem
import com.livebusjourneytracker.core.ui.theme.components.SearchView
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
    val fromFocus = remember { FocusRequester() }
    val toFocus = remember { FocusRequester() }

    uiState.journey?.let { journey ->
        BottomSheetView(journey) { lineId ->
            viewModel.setEvents(BusRouteContract.BusRoutesEvent.FetchLiveBuses(lineId))
        }
    }

    LaunchedEffect(uiState.activeField) {
        when (uiState.activeField) {
            BusRouteContract.ActiveSearchField.FROM -> fromFocus.requestFocus()
            BusRouteContract.ActiveSearchField.TO -> toFocus.requestFocus()
            BusRouteContract.ActiveSearchField.NONE -> Unit
        }
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

        SearchView(promptText = "From", fromFocus, onFromFieldFocused = {
            viewModel.updateFieldFocus(BusRouteContract.ActiveSearchField.FROM)

        }, onSearch = {
            viewModel.searchRoutes(it)
        })

        Spacer(modifier = Modifier.height(16.dp))

        SearchView(
            promptText = "To",
            toFocus,
            onFromFieldFocused = {
                viewModel.updateFieldFocus(BusRouteContract.ActiveSearchField.TO)
            },
            onSearch = {
                viewModel.searchRoutes(it)
            }
        )


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
                            }
                        )

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
                                Card(modifier.clickable {
                                    viewModel.setEvents(
                                        BusRouteContract.BusRoutesEvent.UpdateSelectedDestination(
                                            route
                                        )
                                    )
                                }) {
                                    BusRouteItem(
                                        value = route.name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    )

                                }
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