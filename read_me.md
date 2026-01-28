# Live Bus Journey Tracker

## Architecture Overview

A clean architecture Android app with three main layers:

**UI Layer (Compose)**
- MVVM pattern with StateFlow
- Material Design 3 components
- Loading, empty, and error states

**Domain Layer (Business Logic)**
- Use cases for business operations
- Repository interfaces
- Result<T> pattern for error handling

**Data Layer**
- Repository with API integration
- Retrofit for network calls
- Flow-based reactive streams

**Key Features:**
- Real-time bus tracking with 30s polling
- Journey planning with disambiguation
- Lifecycle-aware background handling
- Functional error handling (no exceptions)
- Modular structure for scalability

---

### 1. How did you infer vehicle position from the available data?

**Data Sources:**
- **Bus Arrivals API**: Provides real-time `timeToStation` (seconds) and `naptanId` for each bus
- **Route Sequence API**: Provides station coordinates (`lat`, `lon`) and polyline data (`lineStrings`) 
- **Journey Planning API**: Provides departure/arrival points for journey planning

**Position Inference Strategy:**
 kotlin
val enrichedArrivals = routeSequenceCache[lineId]?.let { cachedRoute ->
    arrivals.map { arrival ->
        val stop = cachedRoute.stations?.find { it.id == arrival.naptanId }
        arrival.copy(
            lat = stop.lat,      // Static stop position
            lon = stop.lon,      // Static stop position  
            lines = cachedRoute.lineStrings  // Route polylines
        )
    }
}


**Vehicle Position Logic:**
- **Static Stop Positioning**: Vehicles are positioned at bus stop coordinates
- **Time-based Display**: `timeToStation` provides arrival predictions, not real-time vehicle movement
- **Route Visualization**: Polylines show route paths, markers show predicted stop positions

### What assumptions or limitations exist?

**Assumptions:**
- Vehicles are always positioned at the nearest bus stop (no interpolation between stops)
- TfL API provides accurate `timeToStation` predictions
- Route sequences remain static during tracking sessions
- Network connectivity is available for 30-second polling

**Limitations:**
- **No Real-time Vehicle Tracking**: Cannot show buses moving along routes in real-time
- **Stop-based Positioning Only**: Vehicles appear to "jump" between stops rather than smooth movement
- **API Rate Limiting**: 30-second polling interval limits real-time feel
- **Network Dependency**: Offline capability not implemented

## 2. Journey Disambiguation Handling

### How did you model and resolve ambiguous locations?

Disambiguation Data Model:
kotlin
data class BusJourney(
    val fromLocationDisambiguation: Disambiguation?,
    val toLocationDisambiguation: Disambiguation?, 
    val viaLocationDisambiguation: Disambiguation?
)

data class Disambiguation(
    val disambiguationOptions: List<DisambiguationOption>,
    val matchStatus: String?
)

data class DisambiguationOption(
    val parameterValue: String,
    val place: Place,
    val matchQuality: Int?
)


**Resolution Strategy:**
1. *HTTP 300 Detection*: API returns 300 status with disambiguation options
2. *UI Presentation*: Bottom sheet displays multiple location options with place details
3. *User Selection*: User selects correct location from disambiguated list
4. *Retry Logic*: Selected `parameterValue` used in new journey planning request

**Implementation:**
```kotlin
// ViewModel handles disambiguation state
if (journey.requiresDisambiguation()) {
    _uiState.update {
        it.copy(
            journey = journey,
            requiresDisambiguation = true,
            fromDisambiguationOptions = journey.fromLocationDisambiguation?.disambiguationOptions ?: emptyList()
        )
    }
}

// User selection triggers retry with resolved parameters
private fun retryJourneyWithSelectedOptions() {
    val fromLocation = selectedFromOption?.parameterValue ?: currentState.fromLocation
    val toLocation = selectedToOption?.parameterValue ?: currentState.toLocation
    planJourney() // Retry with resolved locations
}
```

## 3. Architecture Decisions

### Why did you choose your architecture?

**Clean Architecture with MVVM Pattern:**

```
UI Layer (Compose) <-- collectAsStateWithLifecycle() <-- ViewModel
       |                                              |  
   User Events --> ViewModel Events --> Use Cases --> Repository --> API
```

**Rationale:**
- Separation of Concerns: Domain logic isolated from UI and data layers
- Testability: Each layer can be unit tested independently
- Reactive UI: Compose + StateFlow provides automatic UI updates
- Lifecycle Awareness: `collectAsStateWithLifecycle()` handles configuration changes
- Maintainability: Clear boundaries make code easier to modify

**Key Components:**
- *Repository Pattern: Abstracts data sources, implements caching strategy
- *Use Cases*: Encapsulate business logic (journey planning, bus tracking)
- *StateFlow*: Reactive state management with lifecycle awareness
- *Dependency Injection*: Koin for clean dependency management

### How is polling coordinated with UI state?

**Polling Coordination Strategy:**

1. **Lifecycle-Aware Polling:**
```kotlin
// Repository: Infinite polling with 30-second intervals
override suspend fun getBusArrivalsById(lineId: String): Flow<List<BusArrival>> = flow {
    while (true) {
        try {
            val arrivalResponse = apiService.getBusRouteArrivalsById(lineId)
            emit(enrichedArrivals)
        } catch (_: Exception) { emit(emptyList()) }
        delay(30_000) // 30-second polling
    }
}

// ViewModel: Job management for lifecycle control
fun fetchLiveBuses(lineId: String) {
    busTrackingJob?.cancel() // Cancel existing
    busTrackingJob = viewModelScope.launch {
        getBusArrivalsUseCase(lineId).collect { arrivals ->
            _uiState.update { it.copy(busArrivals = arrivals) }
        }
    }
}

// UI: Lifecycle-aware collection
LaunchedEffect(uiState.currentTrackingLineId, lifecycleOwner.lifecycle) {
    lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.resumeBusTracking() // Resume when visible
    }
}
```

2. **Cache-First Strategy:**
```kotlin
// Route data cached, arrivals refreshed
private val routeSequenceCache = mutableMapOf<String, BusRouteDto>()

// Cache populated once, reused for all arrival updates
if (!routeSequenceCache.containsKey(lineId)) {
    routeSequenceCache[lineId] = fetchRouteSequence(lineId)
}
```

3. **Background/Foreground Coordination:**
- **Background**: Polling pauses, cache preserved
- **Foreground**: Polling resumes automatically with stored data
- **Journey End**: Cache cleared, polling stopped

## 4. Team & Delivery Perspective

### How would you split this work across 3 mobile engineers?

**Engineer 1: Core Infrastructure & Navigation**
- Journey planning API integration
- Disambiguation flow implementation  
- Navigation between search and map screens
- Error handling and retry logic
- Repository pattern with caching

**Engineer 2: Map Integration & Real-time Data**
- Google Maps integration with custom markers
- Polyline rendering and optimization
- Live bus arrivals polling
- Lifecycle-aware data collection
- Map camera management and animations

**Engineer 3: UI/UX & Polish**
- Search functionality with autocomplete
- Bottom sheet design and animations
- Skeleton loading states
- Material Design 3 theming
- Accessibility improvements

### What would you deliver in Sprint 1 vs Sprint 2?

**Sprint 1 (Core MVP - 2 weeks):**
- Journey planning with from/to search
- Basic disambiguation handling  
- Static map view with route polylines
- Basic bus stop markers
- Navigation between screens
- Error handling

**Sprint 2 (Polish & Real-time - 2 weeks):**
- Live bus arrivals with 30-second polling
- Lifecycle-aware background/foreground handling
- Differentiated markers (start/stops/destination)
- Smooth animations and loading states
- Cache optimization for performance
- Map camera animations

## API Key Implementation

The TfL API key is automatically added to all requests using an OkHttp interceptor:

```kotlin
class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url = original.url.newBuilder()
            .addQueryParameter("app_key", apiKey) 
            .build()
            
        val request = original.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}
```

 *How it works:*
1. Interceptor automatically adds `app_key` query parameter to every API request
2. API key is injected via dependency injection (Koin)
3. All endpoints like `/Line/bus/Arrivals` become `/Line/bus/Arrivals?app_key=YOUR_KEY`
4. No need to manually add the key to each API call

**Configuration:**
- API key provided through DI module
- Secure storage recommended for production apps
- Interceptor ensures consistent authentication across all requests