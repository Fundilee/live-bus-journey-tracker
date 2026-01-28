package com.livebusjourneytracker.core.data.di

import com.livebusjourneytracker.core.data.repository.BusRoutesRepositoryImpl
import com.livebusjourneytracker.core.domain.repository.BusRoutesRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::BusRoutesRepositoryImpl) bind BusRoutesRepository::class
}