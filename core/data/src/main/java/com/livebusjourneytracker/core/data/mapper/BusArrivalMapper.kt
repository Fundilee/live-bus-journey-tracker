package com.livebusjourneytracker.core.data.mapper

import com.livebusjourneytracker.core.data.dto.BusArrivalDto
import com.livebusjourneytracker.core.domain.model.BusArrival

object BusArrivalMapper {
    fun mapToDomain(dto: BusArrivalDto): BusArrival {
        return BusArrival(
            naptanId = dto.naptanId,
            vehicleId = dto.vehicleId,
            stationName = dto.stationName,
            timeToStation = dto.timeToStation,
            modeName = dto.modeName,
            expectedArrival = dto.expectedArrival
        )
    }

    fun mapToDomainList(dtoList: List<BusArrivalDto>): List<BusArrival> {
        return dtoList.map { mapToDomain(it) }
    }
}