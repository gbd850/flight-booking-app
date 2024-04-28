package dev.peter.flightbooking.dto;

import dev.peter.flightbooking.model.Flight;

import java.util.Set;

public record CustomerBookingResponseDto(Set<Flight> bookedFlights) {}
