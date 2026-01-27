package com.livebusjourneytracker.core.ui.theme.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BusRouteItem(
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(11.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
