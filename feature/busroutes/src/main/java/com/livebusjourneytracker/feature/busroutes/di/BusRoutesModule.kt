package com.livebusjourneytracker.feature.busroutes.di

import com.livebusjourneytracker.feature.busroutes.BusRoutesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val busRoutesModule = module {
    viewModelOf(::BusRoutesViewModel)
}