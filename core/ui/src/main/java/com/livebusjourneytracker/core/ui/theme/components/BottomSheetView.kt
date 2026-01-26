package com.livebusjourneytracker.core.ui.theme.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.livebusjourneytracker.core.domain.model.BusJourney
import com.livebusjourneytracker.core.domain.model.DisambiguationOption
import com.livebusjourneytracker.core.domain.model.DisambiguationType
import com.livebusjourneytracker.core.domain.model.Journeys
import com.livebusjourneytracker.core.domain.model.requiresDisambiguation
import com.livebusjourneytracker.core.ui.R
import com.livebusjourneytracker.core.ui.theme.util.formatTime
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetView(
    journey: BusJourney,
    onDisambiguationSelected: (DisambiguationType, DisambiguationOption) -> Unit = { _, _ -> },
    onRetryJourney: () -> Unit = {},
    onDismiss: () -> Unit = {},
    onBusRouteSelected: (Journeys) -> Unit
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
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                )
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
        text = stringResource(R.string.multiple_locations_found),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    Text(
        text = stringResource(R.string.please_select_the_correct_location),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    LazyColumn {
        journey.fromLocationDisambiguation?.let { disambiguation ->
            if (disambiguation.disambiguationOptions.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.from_location),
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

        journey.toLocationDisambiguation?.let { disambiguation ->
            if (disambiguation.disambiguationOptions.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.to_location),
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
                Text(stringResource(R.string.search_with_selected_locations))
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
            .clip(RoundedCornerShape(5.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = option.place.commonName ?: stringResource(R.string.unknown_location),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            option.place.placeType?.let { placeType ->
                Text(
                    text = placeType,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun JourneyResultContent(journey: BusJourney, onJourneySelected: (Journeys) -> Unit) {

    Text(
        text = stringResource(R.string.journey_details),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(journey.journey) { route ->

            val busLeg = route.legs.firstOrNull() ?: return@items

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .clickable {
                        onJourneySelected(route)
                    },
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {

                    busLeg.routeOptions?.firstOrNull()?.name?.let { busNr ->
                        Text(stringResource(R.string.bus, busNr))
                    }

                    Text(
                        stringResource(R.string.from, busLeg.departurePoint.commonName.orEmpty()),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        stringResource(R.string.to, busLeg.arrivalPoint.commonName.orEmpty()),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        stringResource(
                            R.string.departure,
                            formatTime(busLeg.departureTime.orEmpty())
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        stringResource(R.string.arrival, formatTime(busLeg.arrivalTime.orEmpty())),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (journey.journey.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_bus_routes_available),
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}