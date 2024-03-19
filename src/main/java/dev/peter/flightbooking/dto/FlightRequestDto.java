package dev.peter.flightbooking.dto;

import jakarta.persistence.Column;

import java.sql.Timestamp;
import java.util.Date;

public record FlightRequestDto(
        String name,
        Double price,
        Timestamp startDate,
        Timestamp endDate,
        String startLocation,
        String endLocation,
        boolean isAvailable
) {}
