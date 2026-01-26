package com.livebusjourneytracker.core.ui.theme.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.livebusjourneytracker.core.domain.model.BusJourney
import com.livebusjourneytracker.core.domain.model.DisambiguationOption
import com.livebusjourneytracker.core.domain.model.DisambiguationType
import com.livebusjourneytracker.core.domain.model.Journeys
import com.livebusjourneytracker.core.domain.model.requiresDisambiguation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetView(
    journey: BusJourney,
    onDisambiguationSelected: (DisambiguationType, DisambiguationOption) -> Unit = { _, _ -> },
    onRetryJourney: () -> Unit = {},
    onDismiss: () -> Unit = {},
    onBusRouteSelected: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            coroutineScope.launch {
                sheetState.hide()
                onDismiss()
            }
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (journey.requiresDisambiguation()) {
                DisambiguationContent(
                    journey = journey,
                    onDisambiguationSelected = onDisambiguationSelected,
                    onRetryJourney = onRetryJourney
                )
            } else {
                JourneyResultContent(
                    journey = journey,
                    onJourneySelected = {
                        onBusRouteSelected(it)
                    }
                )
            }
        }
    }
}

@Composable
private fun DisambiguationContent(
    journey: BusJourney,
    onDisambiguationSelected: (DisambiguationType, DisambiguationOption) -> Unit,
    onRetryJourney: () -> Unit
) {
    Text(
        text = "Multiple locations found",
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    Text(
        text = "Please select the correct location:",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    LazyColumn {
        // Show FROM location disambiguation
        journey.fromLocationDisambiguation?.let { disambiguation ->
            if (disambiguation.disambiguationOptions.isNotEmpty()) {
                item {
                    Text(
                        text = "FROM Location:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(disambiguation.disambiguationOptions) { option ->
                    DisambiguationOptionItem(
                        option = option,
                        onClick = { onDisambiguationSelected(DisambiguationType.FROM, option) }
                    )
                }
            }
        }

        // Show TO location disambiguation
        journey.toLocationDisambiguation?.let { disambiguation ->
            if (disambiguation.disambiguationOptions.isNotEmpty()) {
                item {
                    Text(
                        text = "TO Location:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(disambiguation.disambiguationOptions) { option ->
                    DisambiguationOptionItem(
                        option = option,
                        onClick = { onDisambiguationSelected(DisambiguationType.TO, option) }
                    )
                }
            }
        }

        item {
            Button(
                onClick = onRetryJourney,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Search with selected locations")
            }
        }
    }
}

@Composable
private fun DisambiguationOptionItem(
    option: DisambiguationOption,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = option.place.commonName ?: "Unknown Location",
                style = MaterialTheme.typography.bodyLarge
            )
            option.place.placeType?.let { placeType ->
                Text(
                    text = placeType,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun JourneyResultContent(journey: BusJourney, onJourneySelected: (String) -> Unit) {

    Text(
        text = "Journey Details",
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(journey.journey) { route ->
            Card(
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp)
                    .clickable {
                        val lineId = getBusLineId(route)
                        if (lineId != null) {
                            onJourneySelected(lineId)
                        }
                    }) {
                route.legs.map { leg ->
                    Column {
                        leg.routeOptions?.name?.let { busNr ->
                            Text(
                                text = "Bus: $busNr",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        leg.departurePoint.commonName?.let { from ->
                            Text(
                                text = "From: $from",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        leg.arrivalPoint.commonName?.let { to ->
                            Text(text = "To: $to", style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(
                            text = "Departure: ${leg.departureTime}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Arrival: ${leg.arrivalTime}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }


        if (journey.journey.isEmpty()) {
            item {
                Text(
                    text = "No bus routes available",
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun getBusLineId(journey: Journeys): String? {
    return journey.legs.firstOrNull()?.routeOptions?.name
}