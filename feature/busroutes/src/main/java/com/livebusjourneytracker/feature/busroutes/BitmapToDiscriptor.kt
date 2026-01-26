package com.livebusjourneytracker.feature.busroutes

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.graphics.createBitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("UseCompatLoadingForDrawables")
fun bitmapDescriptorFromVector(
    context: Context,
    @DrawableRes resId: Int
): BitmapDescriptor {
    val drawable = context.getDrawable(resId)!!
    val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
