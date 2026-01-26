package com.livebusjourneytracker.core.data.mapper

import com.livebusjourneytracker.core.data.dto.BusRouteDto
import com.livebusjourneytracker.core.data.dto.LineStatusDto
import com.livebusjourneytracker.core.data.dto.RouteSectionDto
import com.livebusjourneytracker.core.data.dto.ServiceTypeDto
import com.livebusjourneytracker.core.data.dto.StationsDto
import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.model.LineStatus
import com.livebusjourneytracker.core.domain.model.RouteSection
import com.livebusjourneytracker.core.domain.model.ServiceType
import com.livebusjourneytracker.core.domain.model.Stations

object BusRouteMapper {

    fun mapToDomain(dto: BusRouteDto): BusRoute {
        return BusRoute(
            id = dto.id,
            name = dto.name,
            lat = 0.0,
            lon = 0.0,
            lineStatuses = dto.lineStatuses?.map { mapLineStatusToDomain(it) } ?: emptyList(),
            routeSections = dto.routeSections?.map { mapRouteSectionToDomain(it) } ?: emptyList(),
            serviceTypes = dto.serviceTypes?.map { mapServiceTypeToDomain(it) } ?: emptyList(),
            stations = dto.stations?.map { mapStationToDomain(it) } ?: emptyList(),
            lines = dto.lineStrings ?: emptyList()
        )
    }
    
    fun mapToDomainList(dtoList: List<BusRouteDto>): List<BusRoute> {
        return dtoList.map { mapToDomain(it) }
    }
    
    private fun mapLineStatusToDomain(dto: LineStatusDto): LineStatus {
        return LineStatus(
            id = dto.id,
            statusSeverity = dto.statusSeverity,
            statusSeverityDescription = dto.statusSeverityDescription,
            reason = dto.reason
        )
    }

    private fun mapStationToDomain(dto: StationsDto): Stations {
        return Stations(
            id = dto.id,
            stopType = dto.stopType,
            lat = dto.lat,
            lon = dto.lon,
            name = dto.name,
            status = dto.status
        )
    }
    
    private fun mapRouteSectionToDomain(dto: RouteSectionDto): RouteSection {
        return RouteSection(
            name = dto.name,
            direction = dto.direction,
            originator = dto.originator,
            destination = dto.destination,
            serviceType = dto.serviceType
        )
    }
    
    private fun mapServiceTypeToDomain(dto: ServiceTypeDto): ServiceType {
        return ServiceType(
            name = dto.name,
            uri = dto.uri
        )
    }
}