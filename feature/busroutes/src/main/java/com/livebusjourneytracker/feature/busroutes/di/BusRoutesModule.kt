package com.livebusjourneytracker.feature.busroutes.di

import com.livebusjourneytracker.feature.busroutes.BusRoutesViewModel
import com.livebusjourneytracker.feature.busroutes.data.api.TflApiService
import com.livebusjourneytracker.feature.busroutes.data.repository.BusRoutesRepositoryImpl
import com.livebusjourneytracker.feature.busroutes.domain.repository.BusRoutesRepository
import com.livebusjourneytracker.feature.busroutes.domain.usecase.GetBusRoutesUseCase
import com.livebusjourneytracker.feature.busroutes.domain.usecase.GetNearbyStopsUseCase
import com.livebusjourneytracker.feature.busroutes.domain.usecase.SearchBusRoutesUseCase
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
    
    // ViewModel
    viewModelOf(::BusRoutesViewModel)
}