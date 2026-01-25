package com.livebusjourneytracker.core.ui.theme.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp

@Composable
fun SearchView(
    promptText: String, focusRequester: FocusRequester,
    onSearch: (String) -> Unit, onFromFieldFocused: (Boolean) -> Unit
) {
    var query by remember { mutableStateOf("") }

    OutlinedTextField(
        value = query,
        onValueChange = {
            query = it
            onSearch(query)
        },

        label = { Text(promptText) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        modifier = Modifier
            .focusRequester(focusRequester)
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .onFocusChanged {
                if (it.isFocused && promptText.equals("From")) onFromFieldFocused(true) else onFromFieldFocused(
                    false
                )
            },
        singleLine = true,
    )
}