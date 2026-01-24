package com.livebusjourneytracker.feature.busroutes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.model.BusStop

@Composable
fun BusRoutesMapView(
    routes: List<BusRoute>,
    stops: List<BusStop>,
    modifier: Modifier = Modifier,
    onRouteSelected: (BusRoute) -> Unit = {},
    onStopSelected: (BusStop) -> Unit = {}
) {
    val context = LocalContext.current
    
    // Default to London center
    val londonCenter = LatLng(51.5074, -0.1278)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(londonCenter, 12f)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = false,
            mapType = MapType.NORMAL
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            compassEnabled = true,
            myLocationButtonEnabled = false
        )
    ) {
        // Add bus stop markers
        stops.forEach { stop ->
            val position = LatLng(stop.lat, stop.lon)
            
            Marker(
                state = MarkerState(position = position),
                title = stop.commonName,
                snippet = "Bus Stop ID: ${stop.id}",
                onClick = { marker ->
                    onStopSelected(stop)
                    false // Return false to show info window
                }
            )
        }
        
        // Add polylines for bus routes if we have route geometry data
        routes.forEachIndexed { index, route ->
            // For now, we'll add a circle around London center for each route
            // In a real implementation, you'd get the actual route geometry from the API
            Circle(
                center = londonCenter,
                radius = 1000.0 + (index * 500.0), // Different radius for each route
                strokeColor = getRouteColor(index),
                strokeWidth = 3f,
                fillColor = getRouteColor(index).copy(alpha = 0.1f),
                clickable = true,
                onClick = {
                    onRouteSelected(route)
                }
            )
        }
    }
}

@Composable
private fun getRouteColor(index: Int): Color {
    val colors = listOf(
        Color.Red,
        Color.Blue,
        Color.Green,
        Color.Magenta,
        Color.Cyan,
        Color.Yellow,
        Color.Gray
    )
    return colors[index % colors.size]
}

@Composable
fun BusRouteInfoCard(
    route: BusRoute?,
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
                        text = route.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Route ID: ${route.id}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                if (route.lineStatuses.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Status:",
                        style = MaterialTheme.typography.labelMedium
                    )
                    route.lineStatuses.forEach { status ->
                        Text(
                            text = "• ${status.statusSeverityDescription}",
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
}