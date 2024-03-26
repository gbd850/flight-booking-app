package dev.peter.flightbooking.dto;

import dev.peter.flightbooking.model.Flight;

import java.util.Set;

public record CustomerResponseDto(
        Integer id,

        String username,

        String password,

        String role,

        Set<Flight> bookedFlights
) {
}
