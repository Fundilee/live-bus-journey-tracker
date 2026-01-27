package com.livebusjourneytracker.feature.busroutes.di

import com.livebusjourneytracker.core.data.api.TflApiService
import com.livebusjourneytracker.core.data.repository.BusRoutesRepositoryImpl
import com.livebusjourneytracker.core.domain.repository.BusRoutesRepository
import com.livebusjourneytracker.core.domain.usecase.GetBusArrivalsUseCase
import com.livebusjourneytracker.core.domain.usecase.GetJourneyResultsUseCase
import com.livebusjourneytracker.core.domain.usecase.SearchBusRoutesUseCase
import com.livebusjourneytracker.feature.busroutes.ui.BusRoutesViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

val busRoutesModule = module {
    single<TflApiService> {
        get<Retrofit>().create(TflApiService::class.java)
    }

    singleOf(::BusRoutesRepositoryImpl) bind BusRoutesRepository::class
    singleOf(::SearchBusRoutesUseCase)
    singleOf(::GetBusArrivalsUseCase)
    singleOf(::GetJourneyResultsUseCase)
    viewModelOf(::BusRoutesViewModel)
}