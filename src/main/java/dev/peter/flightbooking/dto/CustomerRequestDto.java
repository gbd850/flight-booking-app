package dev.peter.flightbooking.dto;

public record CustomerRequestDto(
        Integer id,
        String username,
        String password,
        String role
) {}
