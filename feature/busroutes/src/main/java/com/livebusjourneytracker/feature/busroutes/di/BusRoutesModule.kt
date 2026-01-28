package com.livebusjourneytracker.feature.busroutes.di

import com.livebusjourneytracker.core.domain.usecase.GetBusArrivalsUseCase
import com.livebusjourneytracker.core.domain.usecase.GetJourneyResultsUseCase
import com.livebusjourneytracker.core.domain.usecase.SearchBusRoutesUseCase
import com.livebusjourneytracker.feature.busroutes.ui.BusRoutesViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val busRoutesModule = module {
    singleOf(::SearchBusRoutesUseCase)
    singleOf(::GetBusArrivalsUseCase)
    singleOf(::GetJourneyResultsUseCase)
    viewModelOf(::BusRoutesViewModel)
}