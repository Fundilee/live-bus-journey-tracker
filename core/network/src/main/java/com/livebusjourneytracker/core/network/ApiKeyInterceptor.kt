package com.livebusjourneytracker.core.network

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalHttpUrl = original.url
        
        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("app_key", apiKey)
            .build()
            
        val requestBuilder = original.newBuilder().url(url)
        val request = requestBuilder.build()
        
        return chain.proceed(request)
    }
}