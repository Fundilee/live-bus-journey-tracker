package com.livebusjourneytracker.feature.busroutes.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.livebusjourneytracker.core.ui.theme.components.BottomSheetView
import com.livebusjourneytracker.core.ui.theme.components.BusRouteItem
import com.livebusjourneytracker.core.ui.theme.components.SearchView
import com.livebusjourneytracker.feature.busroutes.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    viewModel: BusRoutesViewModel = koinViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var fromQuery by remember { mutableStateOf("") }
    var toQuery by remember { mutableStateOf("") }
    var isMapView by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    val fromFocus = remember { FocusRequester() }
    val toFocus = remember { FocusRequester() }

    uiState.journey?.let { journey ->
        BottomSheetView(
            journey = journey,
            selectedFromOption = uiState.selectedFromOption,
            selectedToOption = uiState.selectedToOption,
            onDisambiguationSelected = { type, option ->
                viewModel.setEvents(
                    BusRouteContract.BusRoutesEvent.SelectDisambiguationOption(
                        type,
                        option
                    )
                )
            },
            onRetryJourney = {
                viewModel.setEvents(BusRouteContract.BusRoutesEvent.RetryJourneyWithSelectedOptions)
            },
            onDismiss = {
                viewModel.clearJourney()
            }
        ) { journey ->
            viewModel.setEvents(BusRouteContract.BusRoutesEvent.FetchLiveBuses(journey))
            viewModel.clearJourney() // Dismiss bottom sheet to show map
        }
    }

    LaunchedEffect(uiState.activeField) {
        when (uiState.activeField) {
            BusRouteContract.ActiveSearchField.FROM -> {
                fromFocus.requestFocus()
                isSearchActive = true
            }

            BusRouteContract.ActiveSearchField.TO -> {
                toFocus.requestFocus()
                isSearchActive = true
            }

            BusRouteContract.ActiveSearchField.NONE -> Unit
        }
    }

    LaunchedEffect(uiState.isBusArrivalSuccess) {
        if (uiState.isBusArrivalSuccess && uiState.busArrivals?.isNotEmpty() == true) {
            isMapView = true
            isSearchActive = false
        }
    }

    LaunchedEffect(uiState.currentTrackingLineId, lifecycleOwner.lifecycle) {
        if (uiState.currentTrackingLineId != null) {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resumeBusTracking()
            }
        }
    }

    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.stopBusTracking()
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
                text = stringResource(R.string.bus_routes),
                style = MaterialTheme.typography.headlineMedium
            )

            IconButton(
                onClick = {
                    // Reset everything to initial state
                    viewModel.resetToInitialState()
                    searchQuery = ""
                    fromQuery = ""
                    toQuery = ""
                    isMapView = false
                    isSearchActive = false
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear all",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SearchView(
            promptText = "From",
            focusRequester = fromFocus,
            value = fromQuery,
            onFromFieldFocused = {
                viewModel.updateFieldFocus(BusRouteContract.ActiveSearchField.FROM)
                isSearchActive = true
            },
            onSearch = {
                fromQuery = it
                searchQuery = it
                if (it.isNotBlank()) {
                    viewModel.searchRoutes(it)
                    isSearchActive = true
                } else {
                    isSearchActive = false
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SearchView(
            promptText = "To",
            focusRequester = toFocus,
            value = toQuery,
            onFromFieldFocused = {
                viewModel.updateFieldFocus(BusRouteContract.ActiveSearchField.TO)
                isSearchActive = true
            },
            onSearch = {
                toQuery = it
                searchQuery = it
                if (it.isNotBlank()) {
                    viewModel.searchRoutes(it)
                    isSearchActive = true
                } else {
                    isSearchActive = false
                }
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
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (uiState.networkError) stringResource(R.string.connection_issue) else stringResource(
                                R.string.error
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            color =
                                MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = uiState.error.toString(),
                            color = if (uiState.networkError)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.retryLastOperation() },
                                enabled = !uiState.isRetrying
                            ) {
                                if (uiState.isRetrying) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Retry")
                                }
                            }

                            Button(
                                onClick = { viewModel.clearError() },
                                enabled = !uiState.isRetrying
                            ) {
                                Text("Dismiss")
                            }
                        }
                    }
                }
            }

            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        isSearchActive -> {
                            LazyColumn {
                                items(uiState.routes) { route ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 4.dp, vertical = 5.dp)
                                            .clickable {
                                                // Update local search field states based on active field
                                                when (uiState.activeField) {
                                                    BusRouteContract.ActiveSearchField.FROM -> {
                                                        fromQuery = route.name
                                                    }

                                                    BusRouteContract.ActiveSearchField.TO -> {
                                                        toQuery = route.name
                                                    }

                                                    else -> {}
                                                }

                                                viewModel.setEvents(
                                                    BusRouteContract.BusRoutesEvent.UpdateSelectedDestination(
                                                        route
                                                    )
                                                )
                                                isSearchActive = false
                                            },
                                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                    ) {
                                        BusRouteItem(
                                            value = route.name,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                        )
                                    }
                                }

                                if (uiState.routes.isEmpty() && searchQuery.isNotBlank()) {
                                    item {
                                        Text(
                                            text = stringResource(
                                                R.string.no_routes_found_for,
                                                searchQuery
                                            ),
                                            modifier = Modifier.padding(16.dp),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        (isMapView || uiState.busArrivals?.isNotEmpty() == true) && !isSearchActive -> {
                            BusRoutesMapView(
                                routes = uiState.busArrivals,
                                busArrival = uiState.fromCoordinates,
                                busDeparture = uiState.toCoordinates,
                                lines = uiState.lines,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }

                        else -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.plan_your_journey),
                                    style = MaterialTheme.typography.headlineSmall,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = stringResource(R.string.search_for_bus_stops_or_enter_locations),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
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