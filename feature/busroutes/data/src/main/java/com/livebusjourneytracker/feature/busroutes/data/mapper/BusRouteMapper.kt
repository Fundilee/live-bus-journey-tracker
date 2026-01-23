package com.livebusjourneytracker.feature.busroutes.data.mapper

import com.livebusjourneytracker.feature.busroutes.data.dto.BusRouteDto
import com.livebusjourneytracker.feature.busroutes.data.dto.LineStatusDto
import com.livebusjourneytracker.feature.busroutes.data.dto.RouteSectionDto
import com.livebusjourneytracker.feature.busroutes.data.dto.ServiceTypeDto
import com.livebusjourneytracker.feature.busroutes.domain.model.BusRoute
import com.livebusjourneytracker.feature.busroutes.domain.model.LineStatus
import com.livebusjourneytracker.feature.busroutes.domain.model.RouteSection
import com.livebusjourneytracker.feature.busroutes.domain.model.ServiceType

object BusRouteMapper {


    fun mapToDomain(dto: BusRouteDto): BusRoute {
        return BusRoute(
            id = dto.id,
            name = dto.name,
            lineStatuses = dto.lineStatuses?.map { mapLineStatusToDomain(it) } ?: emptyList(),
            routeSections = dto.routeSections?.map { mapRouteSectionToDomain(it) } ?: emptyList(),
            serviceTypes = dto.serviceTypes?.map { mapServiceTypeToDomain(it) } ?: emptyList()
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