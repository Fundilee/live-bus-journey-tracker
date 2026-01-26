package com.livebusjourneytracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.livebusjourneytracker.feature.busroutes.ui.LandingScreen

@Composable
fun MainNavGraph(navController: NavHostController, modifier: Modifier) {

    NavHost(navController = navController, startDestination = Destination.LandingScreen) {
        composable<Destination.LandingScreen> {
            LandingScreen(modifier = modifier)
        }
    }
}