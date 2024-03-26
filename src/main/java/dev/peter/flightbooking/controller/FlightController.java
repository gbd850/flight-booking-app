package dev.peter.flightbooking.controller;

import dev.peter.flightbooking.dto.FlightRequestDto;
import dev.peter.flightbooking.dto.FlightResponseDto;
import dev.peter.flightbooking.model.Flight;
import dev.peter.flightbooking.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flight")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    private static final Logger log = LoggerFactory.getLogger(FlightController.class);

    @GetMapping(
            params = {
            "startLocation"
    })
    public ResponseEntity<List<FlightResponseDto>> getFLightsByStartLocation(
            @RequestParam String startLocation,
            @RequestParam(required = false, defaultValue = "false") boolean filterUnavailable
    ) {
//        log.info(Thread.currentThread().toString());
        return new ResponseEntity<>(flightService.getFLightsByStartLocation(startLocation, filterUnavailable), HttpStatus.OK);
    }

    @GetMapping(
            params = {
            "endLocation"
    })
    public ResponseEntity<List<FlightResponseDto>> getFLightsByEndLocation(
            @RequestParam String endLocation,
            @RequestParam(required = false, defaultValue = "false") boolean filterUnavailable
    ) {
        return new ResponseEntity<>(flightService.getFLightsByEndLocation(endLocation, filterUnavailable), HttpStatus.OK);
    }

    @GetMapping(
            params = {
            "startDate",
            "endDate"
    })
    public ResponseEntity<List<FlightResponseDto>> getFLightsByTimeFrame(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false, defaultValue = "false") boolean filterUnavailable
    ) {
        return new ResponseEntity<>(flightService.getFLightsByTimeFrame(startDate, endDate, filterUnavailable), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<FlightResponseDto> createFlight(@RequestBody FlightRequestDto flightRequestDto) {
        return new ResponseEntity<>(flightService.createFlight(flightRequestDto), HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Integer id) {
        flightService.deleteFlight(id);
        return ResponseEntity.ok().build();
    }
}
