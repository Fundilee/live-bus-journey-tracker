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
import com.livebusjourneytracker.core.domain.model.BusArrival
import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.model.BusStop

@Composable
fun BusRoutesMapView(
    routes: List<BusArrival>?,
    stops: List<BusStop>,
    busRoute: BusRoute? = null,
    modifier: Modifier = Modifier,
    onRouteSelected: (BusRoute) -> Unit = {},
    onStopSelected: (BusStop) -> Unit = {}
) {
    val context = LocalContext.current
    
    // Default to London center
    val londonCenter = LatLng(51.5074, -0.1278)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(londonCenter, 10f)
    }
    
    android.util.Log.d("MAP_DEBUG", "Setting up map with center: ${londonCenter.latitude}, ${londonCenter.longitude}")
    android.util.Log.d("MAP_DEBUG", "Number of bus arrivals to plot: ${routes?.size ?: 0}")

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
            android.util.Log.d("MAP_DEBUG", "Google Map loaded successfully")
        }
    ) {
        // Add bus stop markers
        routes?.forEach { route ->
            android.util.Log.d("MAP_DEBUG", "Plotting marker: lat=${route.lat}, lon=${route.lon}, naptanId=${route.naptanId}")
            val position = LatLng(route.lat, route.lon)

            Marker(
                state = MarkerState(position = position),
                title = route.modeName,
                snippet = "Bus Stop ID: ${route.naptanId}",
                onClick = { marker ->
//                    onStopSelected(route)
                    false // Return false to show info window
                }
            )
        }
        
        routes?.let { stations ->
            if (stations.isNotEmpty()) {
                val routePoints = stations.map { station ->
                    LatLng(station.lat, station.lon)
                }
                
                android.util.Log.d("MAP_DEBUG", "Drawing polyline with ${routePoints.size} points")
                
                Polyline(
                    points = routePoints,
                    color = Color.Blue,
                    width = 8f,
                    pattern = null,
                    clickable = true,
                    onClick = {
                        android.util.Log.d("MAP_DEBUG", "Route polyline clicked")
//                        busRoute.let(onRouteSelected)
                    }
                )
            }
        }
        
        // Add polylines for bus routes if we have route geometry data
//        routes?.forEachIndexed { index, route ->
//            // For now, we'll add a circle around London center for each route
//            // In a real implementation, you'd get the actual route geometry from the API
//            Circle(
//                center = londonCenter,
//                radius = 1000.0 + (index * 500.0), // Different radius for each route
//                strokeColor = getRouteColor(index),
//                strokeWidth = 3f,
//                fillColor = getRouteColor(index).copy(alpha = 0.1f),
//                clickable = true,
//                onClick = {
////                    onRouteSelected(route)
//                }
//            )
//        }
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
                    text = "Route ID: ${route.naptanId}",
                    style = MaterialTheme.typography.bodyMedium
                )
                

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Arrival Time: ${route.expectedArrival}",
                        style = MaterialTheme.typography.labelMedium
                    )
            }
        }
    }
}