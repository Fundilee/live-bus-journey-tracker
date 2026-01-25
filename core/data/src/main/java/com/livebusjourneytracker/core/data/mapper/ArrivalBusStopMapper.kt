package com.livebusjourneytracker.core.data.mapper

import com.livebusjourneytracker.core.data.dto.ArrivalsBusStopsDto
import com.livebusjourneytracker.core.data.dto.BusRouteDto
import com.livebusjourneytracker.core.data.dto.LineStatusDto
import com.livebusjourneytracker.core.data.dto.RouteSectionDto
import com.livebusjourneytracker.core.data.dto.ServiceTypeDto
import com.livebusjourneytracker.core.domain.model.ArrivalBusStop
import com.livebusjourneytracker.core.domain.model.BusRoute
import com.livebusjourneytracker.core.domain.model.LineStatus
import com.livebusjourneytracker.core.domain.model.RouteSection
import com.livebusjourneytracker.core.domain.model.ServiceType

object ArrivalBusStopMapper {
    fun mapToDomain(dto: ArrivalsBusStopsDto): ArrivalBusStop {
        return ArrivalBusStop(
            naptanId = dto.naptanId,
            vehicleId = dto.vehicleId,
            stationName = dto.stationName,
            timeToStation = dto.timeToStation
        )
    }

    fun mapToDomainList(dtoList: List<ArrivalsBusStopsDto>): List<ArrivalBusStop> {
        return dtoList.map { mapToDomain(it) }
    }
}