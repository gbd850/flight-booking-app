package dev.peter.flightbooking.dto;

public record CustomerRequestDto(
        String username,
        String password,
        String role
) {}
