package com.livebusjourneytracker.core.data.mapper

import android.util.Log
import com.livebusjourneytracker.core.data.dto.AdditionalPropertyDto
import com.livebusjourneytracker.core.data.dto.DisambiguationDto
import com.livebusjourneytracker.core.data.dto.DisambiguationOptionDto
import com.livebusjourneytracker.core.data.dto.JourneyDto
import com.livebusjourneytracker.core.data.dto.JourneyResponseDto
import com.livebusjourneytracker.core.data.dto.JourneyVectorDto
import com.livebusjourneytracker.core.data.dto.LegDto
import com.livebusjourneytracker.core.data.dto.ModeDto
import com.livebusjourneytracker.core.data.dto.PlaceDto
import com.livebusjourneytracker.core.data.dto.RouteOptionDto
import com.livebusjourneytracker.core.data.dto.SearchCriteriaDto
import com.livebusjourneytracker.core.domain.model.AdditionalProperty
import com.livebusjourneytracker.core.domain.model.BusJourney
import com.livebusjourneytracker.core.domain.model.Disambiguation
import com.livebusjourneytracker.core.domain.model.DisambiguationOption
import com.livebusjourneytracker.core.domain.model.JourneyVector
import com.livebusjourneytracker.core.domain.model.Journeys
import com.livebusjourneytracker.core.domain.model.Legs
import com.livebusjourneytracker.core.domain.model.Mode
import com.livebusjourneytracker.core.domain.model.Place
import com.livebusjourneytracker.core.domain.model.RouteOption
import com.livebusjourneytracker.core.domain.model.SearchCriteria

object BusJourneyMapper {

    fun mapToDomain(dto: JourneyResponseDto): BusJourney {
        Log.d("Mapper is called", dto.journeys.toString())
        return BusJourney(
            toLocationDisambiguation = dto.toLocationDisambiguation?.let {
                mapDisambiguationToDomain(
                    it
                )
            },
            fromLocationDisambiguation = dto.fromLocationDisambiguation?.let {
                mapDisambiguationToDomain(
                    it
                )
            },
            viaLocationDisambiguation = dto.viaLocationDisambiguation?.let {
                mapDisambiguationToDomain(
                    it
                )
            },
            recommendedMaxAgeMinutes = dto.recommendedMaxAgeMinutes,
            searchCriteria = dto.searchCriteria?.let { mapSearchCriteriaToDomain(it) },
            journeyVector = dto.journeyVector?.let { mapJourneyVectorToDomain(it) },
            journey = dto.journeys?.let { it -> it.map { mapJourneyToDomain(it) } } ?: emptyList()
        )

    }

    private fun mapDisambiguationToDomain(dto: DisambiguationDto): Disambiguation {
        return Disambiguation(
            disambiguationOptions = dto.disambiguationOptions.map {
                mapDisambiguationOptionToDomain(
                    it
                )
            },
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

    private fun mapModeToDomain(dto: ModeDto): Mode {
        Log.d("Mapper is called mode", dto.toString())
        return Mode(
            id = dto.id,
            name = dto.name
        )
    }

    private fun mapRouteOptionToDomain(dto: RouteOptionDto): RouteOption {
        Log.d("Mapper is called option", dto.toString())
        return RouteOption(
            name = dto.name
        )
    }

    private fun mapPlaceToDomain(dto: PlaceDto): Place {
        Log.d("Mapper is called option", dto.toString())
        return Place(
            naptanId = dto.naptanId,
            icsCode = dto.icsCode,
            stopType = dto.stopType,
            url = dto.url,
            commonName = dto.commonName,
            placeType = dto.placeType,
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

    private fun mapLegsToDomain(dto: LegDto): Legs {
        Log.d("Mapper is called legs", dto.toString())
        return Legs(
            duration = dto.duration ?: 0,
            departureTime = dto.departureTime,
            arrivalTime = dto.arrivalTime,
            departurePoint = mapPlaceToDomain(dto.departurePoint),
            arrivalPoint = mapPlaceToDomain(dto.arrivalPoint),
            mode = dto.mode?.let { mapModeToDomain(it) },
            routeOptions = dto.routeOptions?.let { mapRouteOptionToDomain(it) },
            lineId = dto.lineId
        )
    }

    private fun mapJourneyToDomain(dto: JourneyDto): Journeys {
        Log.d("Mapper is called2", dto.toString())

        return Journeys(
            startDateTime = dto.startDateTime,
            arrivalDateTime = dto.arrivalDateTime,
            duration = dto.duration,
            legs = dto.legs.map { mapLegsToDomain(it) },
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