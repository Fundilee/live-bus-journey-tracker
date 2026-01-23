package com.livebusjourneytracker.di

import com.livebusjourneytracker.BuildConfig
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single<String>(named("apiKey")) { BuildConfig.TFL_API_KEY }
    single<String>(named("baseUrl")) { BuildConfig.BASE_URL }
}