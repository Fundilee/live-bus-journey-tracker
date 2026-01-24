package com.livebusjourneytracker.feature.busroutes.di

import com.livebusjourneytracker.feature.busroutes.BusRoutesViewModel
import com.livebusjourneytracker.core.data.api.TflApiService
import com.livebusjourneytracker.core.data.repository.BusRoutesRepositoryImpl
import com.livebusjourneytracker.core.domain.repository.BusRoutesRepository
import com.livebusjourneytracker.core.domain.usecase.GetBusRoutesUseCase
import com.livebusjourneytracker.core.domain.usecase.GetJourneyResultsUseCase
import com.livebusjourneytracker.core.domain.usecase.GetNearbyStopsUseCase
import com.livebusjourneytracker.core.domain.usecase.SearchBusRoutesUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

val busRoutesModule = module {
    // API Service
    single<TflApiService> {
        get<Retrofit>().create(TflApiService::class.java)
    }
    
    // Repository
    singleOf(::BusRoutesRepositoryImpl) bind BusRoutesRepository::class
    
    // Use Cases
    singleOf(::GetBusRoutesUseCase)
    singleOf(::SearchBusRoutesUseCase)
    singleOf(::GetNearbyStopsUseCase)
    singleOf(::GetJourneyResultsUseCase)

    // ViewModel
    viewModelOf(::BusRoutesViewModel)
}