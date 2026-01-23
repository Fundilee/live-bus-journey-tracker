package com.livebusjourneytracker.navigation

import kotlinx.serialization.Serializable

sealed class Destination {

    @Serializable
    data object LandingScreen : Destination()

    @Serializable
    data object SearchScreen : Destination()

    @Serializable
    data object ConfirmationScreen : Destination()
}