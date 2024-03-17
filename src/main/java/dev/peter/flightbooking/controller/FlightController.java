package dev.peter.flightbooking.controller;

import dev.peter.flightbooking.model.Flight;
import dev.peter.flightbooking.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/flight")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping
    public ResponseEntity<List<Flight>> getFLightsByStartLocation(
            @RequestParam String startLocation,
            @RequestParam boolean filterUnavailable
    ) {
        return new ResponseEntity<>(flightService.getFLightsByStartLocation(startLocation, filterUnavailable), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Flight>> getFLightsByEndLocation(
            @RequestParam String endLocation,
            @RequestParam boolean filterUnavailable
    ) {
        return new ResponseEntity<>(flightService.getFLightsByEndLocation(endLocation, filterUnavailable), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Flight>> getFLightsByTimeFrame(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam boolean filterUnavailable
    ) {
        return new ResponseEntity<>(flightService.getFLightsByTimeFrame(startDate, endDate, filterUnavailable), HttpStatus.OK);
    }
}
