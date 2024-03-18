package dev.peter.flightbooking.dto;

import jakarta.persistence.Column;

import java.util.Date;

public record FlightRequestDto(
        String name,
        Double price,
        Date startDate,
        Date endDate,
        String startLocation,
        String endLocation,
        boolean isAvailable
) {}
