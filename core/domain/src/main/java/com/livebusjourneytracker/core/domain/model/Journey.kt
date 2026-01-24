package com.livebusjourneytracker.core.domain.model

data class Journey(
    val toLocationDisambiguation: Disambiguation?,
    val fromLocationDisambiguation: Disambiguation?,
    val viaLocationDisambiguation: Disambiguation?,
    val recommendedMaxAgeMinutes: Int?,
    val searchCriteria: SearchCriteria?,
    val journeyVector: JourneyVector?
)

data class Disambiguation(
    val disambiguationOptions: List<DisambiguationOption> = emptyList(),
    val matchStatus: String?
)

data class DisambiguationOption(
    val parameterValue: String,
    val uri: String,
    val place: Place,
    val matchQuality: Int
)

data class AdditionalProperty(
    val category: String? = null,
    val key: String? = null,
    val value: String? = null
)

data class Place(
    val naptanId: String? = null,
    val modes: List<String>? = null,
    val icsCode: String? = null,
    val stopType: String? = null,
    val url: String?,
    val commonName: String?,
    val placeType: String?,
    val additionalProperties: List<AdditionalProperty> = emptyList(),
    val lat: Double?,
    val lon: Double?
)

data class SearchCriteria(
    val dateTime: String?,
    val dateTimeType: String?
)

data class JourneyVector(
    val from: String?,
    val to: String?,
    val via: String?,
    val uri: String?
)

// Extension functions for journey state detection
fun Journey.requiresDisambiguation(): Boolean {
    return (fromLocationDisambiguation?.hasOptions() == true) || 
           (toLocationDisambiguation?.hasOptions() == true) ||
           (viaLocationDisambiguation?.hasOptions() == true)
}

fun Journey.isValidJourneyResult(): Boolean {
    return !requiresDisambiguation() && journeyVector != null
}

fun Journey.getDisambiguationNeeded(): List<DisambiguationType> {
    val types = mutableListOf<DisambiguationType>()
    
    if (fromLocationDisambiguation?.hasOptions() == true) {
        types.add(DisambiguationType.FROM)
    }
    if (toLocationDisambiguation?.hasOptions() == true) {
        types.add(DisambiguationType.TO)
    }
    if (viaLocationDisambiguation?.hasOptions() == true) {
        types.add(DisambiguationType.VIA)
    }
    
    return types
}

fun Disambiguation.hasOptions(): Boolean {
    return disambiguationOptions.isNotEmpty()
}

enum class DisambiguationType {
    FROM, TO, VIA
}