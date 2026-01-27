package com.livebusjourneytracker.core.ui.theme.util

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatTime(isoDateTime: String): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.parse(isoDateTime)
            .format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()))
    } else {
        val input = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss",
            Locale.getDefault()
        )
        val output = SimpleDateFormat("HH:mm", Locale.getDefault())
        output.format(input.parse(isoDateTime)!!)
    }
}
