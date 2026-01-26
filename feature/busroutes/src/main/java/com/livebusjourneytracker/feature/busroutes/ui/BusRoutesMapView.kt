package com.livebusjourneytracker.feature.busroutes.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.livebusjourneytracker.core.domain.model.BusArrival
import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.model.BusStop
import com.livebusjourneytracker.core.ui.R
import com.livebusjourneytracker.feature.busroutes.bitmapDescriptorFromVector

@Composable
fun BusRoutesMapView(
    routes: List<BusArrival>?,
    stops: List<BusStop>,
    busRoute: BusRoute? = null,
    modifier: Modifier = Modifier,
    onRouteSelected: (BusRoute) -> Unit = {},
    onStopSelected: (BusStop) -> Unit = {},
    busArrival: BusRouteContract.BusCoordinates?,
    busDeparture: BusRouteContract.BusCoordinates?,
    lines: List<List<BusRouteContract.BusCoordinates>>?,
) {
    val context = LocalContext.current
    val polylines = lines?.map { line ->
        line.map { coord -> LatLng(coord.lat ?: 0.0, coord.lon ?: 0.0) }
    }
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(polylines) {
        val allPoints = polylines?.flatten()
        if (allPoints?.isNotEmpty() == true) {
            val bounds = LatLngBounds.builder().apply {
                allPoints.forEach { include(it) }
            }.build()
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngBounds(bounds, 100)
            )
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = false,
            mapType = MapType.NORMAL,
            isTrafficEnabled = true
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            compassEnabled = true,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = true,
            zoomGesturesEnabled = true,
            scrollGesturesEnabled = true,
            tiltGesturesEnabled = true,
            rotationGesturesEnabled = true
        ),
        onMapLoaded = {
            Log.d("Map", "Google Map loaded successfully")
        }
    ) {
        busDeparture?.let { departure ->
            if (departure.lat != null && departure.lon != null) {
                Marker(
                    state = MarkerState(LatLng(departure.lat, departure.lon)),
                    icon = bitmapDescriptorFromVector(
                        context,
                        R.drawable.ic_bus_filled
                    ),
                    snippet = "Journey start point",
                    title = "Start"
                )
            }
        }

        busArrival?.let { arrival ->
            if (arrival.lat != null && arrival.lon != null) {
                Marker(
                    state = MarkerState(LatLng(arrival.lat, arrival.lon)),
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                    snippet = "Journey destination",
                    title = "End"
                )
            }
        }
        routes?.forEach { route ->
            val position = LatLng(route.lat, route.lon)
            val arrivalTime = formatTimeToStation(route.timeToStation)

            Marker(
                state = MarkerState(position = position),
                title = route.modeName,
                snippet = arrivalTime,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN),
                onClick = {
                    false
                }
            )
        }

        polylines?.forEach { points ->
            if (points.size >= 2) {
                Polyline(
                    points = points,
                    color = Color.Blue,
                    width = 6f
                )
            }
        }
    }
}

@Composable
fun BusRouteInfoCard(
    route: BusArrival?,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    if (route != null) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = route.modeName,
                        style = MaterialTheme.typography.titleLarge
                    )
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(
                        com.livebusjourneytracker.feature.busroutes.R.string.route_id,
                        route.naptanId
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )


                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        com.livebusjourneytracker.feature.busroutes.R.string.arrival_time,
                        route.expectedArrival
                    ),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

fun formatTimeToStation(seconds: Double): String {
    return when {
        seconds <= 0 -> "Arriving"
        seconds < 60 -> "${seconds.toInt()}sec"
        seconds < 180 -> "${(seconds / 60).toInt()}min"
        seconds < 3600 -> {
            val minutes = (seconds / 60).toInt()
            "${minutes}min"
        }
        else -> {
            val hours = (seconds / 3600).toInt()
            val minutes = ((seconds % 3600) / 60).toInt()
            if (minutes == 0) {
                "${hours}hr"
            } else {
                "${hours}hr ${minutes}min"
            }
        }
    }
}