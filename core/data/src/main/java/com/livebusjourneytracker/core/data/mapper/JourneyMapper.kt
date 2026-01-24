package com.livebusjourneytracker.core.data.mapper

import com.livebusjourneytracker.core.data.dto.*
import com.livebusjourneytracker.core.domain.model.*

object JourneyMapper {
    
    fun mapToDomain(dto: JourneyResponseDto): Journey {
        return Journey(
            toLocationDisambiguation = dto.toLocationDisambiguation?.let { mapDisambiguationToDomain(it) },
            fromLocationDisambiguation = dto.fromLocationDisambiguation?.let { mapDisambiguationToDomain(it) },
            viaLocationDisambiguation = dto.viaLocationDisambiguation?.let { mapDisambiguationToDomain(it) },
            recommendedMaxAgeMinutes = dto.recommendedMaxAgeMinutes,
            searchCriteria = dto.searchCriteria?.let { mapSearchCriteriaToDomain(it) },
            journeyVector = dto.journeyVector?.let { mapJourneyVectorToDomain(it) }
        )
    }
    
    private fun mapDisambiguationToDomain(dto: DisambiguationDto): Disambiguation {
        return Disambiguation(
            disambiguationOptions = dto.disambiguationOptions.map { mapDisambiguationOptionToDomain(it) },
            matchStatus = dto.matchStatus
        )
    }
    
    private fun mapDisambiguationOptionToDomain(dto: DisambiguationOptionDto): DisambiguationOption {
        return DisambiguationOption(
            parameterValue = dto.parameterValue,
            uri = dto.uri,
            place = mapPlaceToDomain(dto.place),
            matchQuality = dto.matchQuality
        )
    }
    
    private fun mapPlaceToDomain(dto: PlaceDto): Place {
        return Place(
            naptanId = dto.naptanId,
            modes = dto.modes,
            icsCode = dto.icsCode,
            stopType = dto.stopType,
            url = dto.url,
            commonName = dto.commonName,
            placeType = dto.placeType,
            additionalProperties = dto.additionalProperties.map { mapAdditionalPropertyToDomain(it) },
            lat = dto.lat,
            lon = dto.lon
        )
    }
    
    private fun mapAdditionalPropertyToDomain(dto: AdditionalPropertyDto): AdditionalProperty {
        return AdditionalProperty(
            category = dto.category,
            key = dto.key,
            value = dto.value
        )
    }
    
    private fun mapSearchCriteriaToDomain(dto: SearchCriteriaDto): SearchCriteria {
        return SearchCriteria(
            dateTime = dto.dateTime,
            dateTimeType = dto.dateTimeType
        )
    }
    
    private fun mapJourneyVectorToDomain(dto: JourneyVectorDto): JourneyVector {
        return JourneyVector(
            from = dto.from,
            to = dto.to,
            via = dto.via,
            uri = dto.uri
        )
    }
}