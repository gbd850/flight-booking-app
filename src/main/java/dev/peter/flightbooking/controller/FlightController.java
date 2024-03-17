package dev.peter.flightbooking.controller;

import dev.peter.flightbooking.dto.FlightRequestDto;
import dev.peter.flightbooking.model.Flight;
import dev.peter.flightbooking.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<Flight> createFlight(@RequestBody FlightRequestDto flightRequestDto) {
        return new ResponseEntity<>(flightService.createFlight(flightRequestDto), HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Integer id) {
        flightService.deleteFlight(id);
        return ResponseEntity.ok().build();
    }
}
