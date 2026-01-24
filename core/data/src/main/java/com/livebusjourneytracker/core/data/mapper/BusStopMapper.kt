package com.livebusjourneytracker.core.data.mapper

import com.livebusjourneytracker.core.data.dto.BusLineDto
import com.livebusjourneytracker.core.data.dto.BusStopDto
import com.livebusjourneytracker.core.data.dto.StopPointDto
import com.livebusjourneytracker.core.domain.model.BusLine
import com.livebusjourneytracker.core.domain.model.BusStop
import com.livebusjourneytracker.core.domain.model.StopPoint

object BusStopMapper {
    
    fun mapToDomain(dto: BusStopDto): BusStop {
        return BusStop(
            id = dto.id,
            name = dto.name,
            commonName = dto.commonName,
            distance = dto.distance,
            bearing = dto.bearing,
            naptanId = dto.naptanId,
            lat = dto.lat,
            lon = dto.lon,
            stopType = dto.stopType,
            zone = dto.zone,
            lines = dto.lines ?: emptyList()
        )
    }
    
    fun mapStopPointToDomain(dto: StopPointDto): StopPoint {
        return StopPoint(
            naptanId = dto.naptanId,
            commonName = dto.commonName,
            distance = dto.distance,
            modes = dto.modes,
            icsCode = dto.icsCode,
            smsCode = dto.smsCode,
            stopType = dto.stopType,
            accessibilitySummary = dto.accessibilitySummary,
            lat = dto.lat,
            lon = dto.lon,
            lines = dto.lines?.map { mapBusLineToDomain(it) } ?: emptyList()
        )
    }
    
    fun mapToDomainList(dtoList: List<BusStopDto>): List<BusStop> {
        return dtoList.map { mapToDomain(it) }
    }
    
    fun mapStopPointsToDomainList(dtoList: List<StopPointDto>): List<StopPoint> {
        return dtoList.map { mapStopPointToDomain(it) }
    }
    
    private fun mapBusLineToDomain(dto: BusLineDto): BusLine {
        return BusLine(
            id = dto.id,
            name = dto.name,
            uri = dto.uri,
            fullName = dto.fullName,
            type = dto.type,
            routeType = dto.routeType,
            status = dto.status
        )
    }
    
    fun convertStopPointToBusStop(stopPoint: StopPoint): BusStop {
        return BusStop(
            id = stopPoint.naptanId,
            name = stopPoint.commonName,
            commonName = stopPoint.commonName,
            distance = stopPoint.distance,
            bearing = null,
            naptanId = stopPoint.naptanId,
            lat = stopPoint.lat,
            lon = stopPoint.lon,
            stopType = stopPoint.stopType,
            zone = null,
            lines = stopPoint.lines.map { it.id }
        )
    }
}