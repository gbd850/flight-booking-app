package dev.peter.flightbooking.dto;

import java.sql.Timestamp;

public record FlightResponseDto(
        Integer id,

        String name,

        Double price,

        Timestamp startDate,

        Timestamp endDate,

        String startLocation,

        String endLocation,

        boolean isAvailable
) {
}
