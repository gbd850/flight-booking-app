package dev.peter.flightbooking.service;

import dev.peter.flightbooking.dto.FlightRequestDto;
import dev.peter.flightbooking.dto.FlightResponseDto;
import dev.peter.flightbooking.model.Flight;
import dev.peter.flightbooking.repository.FlightRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@RateLimiter(name = "simpleRateLimit")
public class FlightService {

    private final FlightRepository flightRepository;

    private final CacheManager cacheManager;

    private List<Flight> filterAvailableFlights(List<Flight> flights) {
        return flights.stream()
                .filter(Flight::isAvailable).toList();
    }

    //    @Cacheable(value = "flightStartLocation", key = "#startLocation")
    private List<Flight> getAllFLightsByStartLocation(String startLocation) {

        List<Flight> flights;
        try {
            flights = (List<Flight>) Objects.requireNonNull(cacheManager.getCache("flightStartLocation")).get(startLocation, List.class);
            Objects.requireNonNull(flights);
        } catch (NullPointerException e) {
            flights = Collections.unmodifiableList(flightRepository.findByStartLocation(startLocation));
            cacheManager.getCache("flightStartLocation").putIfAbsent(startLocation, flights);
        }

        if (flights.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Flights not found", new Throwable("Could not find flights matching " + startLocation + " as start location"));
        }
        return flights;
    }

//    @PreAuthorize("hasAuthority('SCOPE_user.read')")
    public List<FlightResponseDto> getFLightsByStartLocation(String startLocation, boolean filterUnavailable) {

        List<Flight> flights = getAllFLightsByStartLocation(startLocation);

        if (filterUnavailable) {
            flights = filterAvailableFlights(flights);
        }

        return flights.stream()
                .map(flight -> new FlightResponseDto(
                        flight.getId(),
                        flight.getName(),
                        flight.getPrice(),
                        flight.getStartDate(),
                        flight.getEndDate(),
                        flight.getStartLocation(),
                        flight.getEndLocation(),
                        flight.isAvailable()
                )).collect(Collectors.toList());
    }

    //    @Cacheable(value = "flightEndLocation", key = "#endLocation")
    private List<Flight> getAllFLightsByEndLocation(String endLocation) {

        List<Flight> flights;
        try {
            flights = (List<Flight>) Objects.requireNonNull(cacheManager.getCache("flightEndLocation")).get(endLocation, List.class);
            Objects.requireNonNull(flights);
        } catch (NullPointerException e) {
            flights = Collections.unmodifiableList(flightRepository.findByEndLocation(endLocation));
            cacheManager.getCache("flightEndLocation").putIfAbsent(endLocation, flights);
        }

        if (flights.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Flights not found", new Throwable("Could not find flights matching " + endLocation + " as end location"));
        }
        return flights;
    }

//    @PreAuthorize("hasAuthority('SCOPE_user.read')")
    public List<FlightResponseDto> getFLightsByEndLocation(String endLocation, boolean filterUnavailable) {

        List<Flight> flights = getAllFLightsByEndLocation(endLocation);

        if (filterUnavailable) {
            flights = filterAvailableFlights(flights);
        }

        return flights.stream()
                .map(flight -> new FlightResponseDto(
                        flight.getId(),
                        flight.getName(),
                        flight.getPrice(),
                        flight.getStartDate(),
                        flight.getEndDate(),
                        flight.getStartLocation(),
                        flight.getEndLocation(),
                        flight.isAvailable()
                )).collect(Collectors.toList());
    }

    //    @Cacheable(value = "flightTimeFrame", key = "{T(java.time.LocalDate).parse(#startDate), T(java.time.LocalDate).parse(#endDate)}")
    private List<Flight> getAllFLightsByTimeFrame(String startDate, String endDate) {

        List<Flight> flights;
        try {
            flights = (List<Flight>) Objects.requireNonNull(cacheManager.getCache("flightTimeFrame")).get(List.of(startDate, endDate), List.class);
            Objects.requireNonNull(flights);
        } catch (NullPointerException e) {
            flights = Collections.unmodifiableList(flightRepository.findByStartDateAndEndDate(startDate, endDate));
            cacheManager.getCache("flightTimeFrame").putIfAbsent(List.of(startDate, endDate), flights);
        }

        if (flights.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Flights not found", new Throwable("Could not find flights matching time frame " + startDate + " - " + endDate));
        }
        return flights;
    }

    //    @Cacheable(value = "flightTimeFrame", key = "{T(java.time.LocalDate).parse(#startDate), T(java.time.LocalDate).parse(#endDate)}")
//    @PreAuthorize("hasAuthority('SCOPE_user.read')")
    public List<FlightResponseDto> getFLightsByTimeFrame(String startDate, String endDate, boolean filterUnavailable) {

        List<Flight> flights = getAllFLightsByTimeFrame(startDate, endDate);

        if (filterUnavailable) {
            flights = filterAvailableFlights(flights);
        }

        return flights.stream()
                .map(flight -> new FlightResponseDto(
                        flight.getId(),
                        flight.getName(),
                        flight.getPrice(),
                        flight.getStartDate(),
                        flight.getEndDate(),
                        flight.getStartLocation(),
                        flight.getEndLocation(),
                        flight.isAvailable()
                )).collect(Collectors.toList());
    }

    @Caching(evict = {
            @CacheEvict(value = "flightStartLocation", key = "{#flightRequestDto.startLocation}"),
            @CacheEvict(value = "flightEndLocation", key = "{#flightRequestDto.endLocation}", condition = "#flightRequestDto.endLocation != null"),
            @CacheEvict(value = "flightTimeFrame", key = "{" +
                    "#flightRequestDto.startDate.toLocalDateTime().toLocalDate()," +
                    "#flightRequestDto.endDate.toLocalDateTime().toLocalDate()" +
                    "}")
    }
    )
    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_user.write')")
    public FlightResponseDto createFlight(FlightRequestDto flightRequestDto) {

        Flight flight = new Flight(
                null,
                flightRequestDto.name(),
                flightRequestDto.price(),
                flightRequestDto.startDate(),
                flightRequestDto.endDate(),
                flightRequestDto.startLocation(),
                flightRequestDto.endLocation(),
                flightRequestDto.isAvailable()
        );

        try {
            flightRepository.save(flight);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong", new Throwable("Something went wrong while creating flight"));
        }

        return new FlightResponseDto(
                flight.getId(),
                flight.getName(),
                flight.getPrice(),
                flight.getStartDate(),
                flight.getEndDate(),
                flight.getStartLocation(),
                flight.getEndLocation(),
                flight.isAvailable()
        );
    }

    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_user.write')")
    public void deleteFlight(Integer id) {

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Flight not found", new Throwable("Flight with id " + id + " does not exist")));

        cacheManager.getCache("flightStartLocation").evictIfPresent(flight.getStartLocation());
        cacheManager.getCache("flightEndLocation").evictIfPresent(flight.getEndLocation());
        cacheManager.getCache("flightTimeFrame").evictIfPresent(List.of(
                flight.getStartDate().toLocalDateTime().toLocalDate(),
                flight.getEndDate().toLocalDateTime().toLocalDate()
        ));

        flightRepository.delete(flight);

    }

    @Caching(evict = {
            @CacheEvict(value = "flightStartLocation", key = "{#flightRequestDto.startLocation}", condition = "#flightRequestDto.startLocation != null"),
            @CacheEvict(value = "flightEndLocation", key = "{#flightRequestDto.endLocation}", condition = "#flightRequestDto.endLocation != null"),
            @CacheEvict(value = "flightTimeFrame", key = "{" +
                    "#flightRequestDto.startDate.toLocalDateTime().toLocalDate()," +
                    "#flightRequestDto.endDate.toLocalDateTime().toLocalDate()" +
                    "}")
    }
    )
    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_user.write')")
    public FlightResponseDto editFlight(Integer id, FlightRequestDto flightRequestDto) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Flight not found", new Throwable("Flight with id " + id + " does not exist")));

        flight.updateEntityFromDto(flightRequestDto);

        flightRepository.save(flight);

        return new FlightResponseDto(
                flight.getId(),
                flight.getName(),
                flight.getPrice(),
                flight.getStartDate(),
                flight.getEndDate(),
                flight.getStartLocation(),
                flight.getEndLocation(),
                flight.isAvailable()
        );
    }
}
