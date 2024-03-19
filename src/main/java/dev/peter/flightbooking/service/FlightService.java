package dev.peter.flightbooking.service;

import dev.peter.flightbooking.dto.FlightRequestDto;
import dev.peter.flightbooking.model.Flight;
import dev.peter.flightbooking.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;

    private final CacheManager cacheManager;

    private List<Flight> filterAvailableFlights(List<Flight> flights) {
        return flights.stream()
                .filter(Flight::isAvailable).toList();
    }

    @Cacheable(value = "flightStartLocation", key = "#startLocation")
    private List<Flight> getAllFLightsByStartLocation(String startLocation) {
        List<Flight> flights = Collections.unmodifiableList(flightRepository.findByStartLocation(startLocation));

        if (flights.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find flights matching start location");
        }
        return flights;
    }

    public List<Flight> getFLightsByStartLocation(String startLocation, boolean filterUnavailable) {

        List<Flight> flights = getAllFLightsByStartLocation(startLocation);

        if (filterUnavailable) {
            flights = filterAvailableFlights(flights);
        }

        return flights;
    }

    @Cacheable(value = "flightEndLocation", key = "#endLocation")
    private List<Flight> getAllFLightsByEndLocation(String endLocation) {

        List<Flight> flights = Collections.unmodifiableList(flightRepository.findByEndLocation(endLocation));

        if (flights.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find flights matching end location");
        }
        return flights;
    }

    public List<Flight> getFLightsByEndLocation(String endLocation, boolean filterUnavailable) {

        List<Flight> flights = getAllFLightsByEndLocation(endLocation);

        if (filterUnavailable) {
            flights = filterAvailableFlights(flights);
        }

        return flights;
    }


    private List<Flight> getAllFLightsByTimeFrame(String startDate, String endDate) {

        List<Flight> flights = Collections.unmodifiableList(flightRepository.findByStartDateAndEndDate(startDate, endDate));

        if (flights.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find flights matching time frame");
        }
        return flights;
    }

    @Cacheable(value = "flightTimeFrame", key = "{T(java.time.LocalDate).parse(#startDate), T(java.time.LocalDate).parse(#endDate)}")
    public List<Flight> getFLightsByTimeFrame(String startDate, String endDate, boolean filterUnavailable) {

        List<Flight> flights = getAllFLightsByTimeFrame(startDate, endDate);

        if (filterUnavailable) {
            flights = filterAvailableFlights(flights);
        }

        return flights;
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
    public Flight createFlight(FlightRequestDto flightRequestDto) {

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
        }

        return flight;
    }

    public void deleteFlight(Integer id) {

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Flight not found"));

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
    public Flight editFlight(Integer id, FlightRequestDto flightRequestDto) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Flight not found"));

        flight.updateEntityFromDto(flightRequestDto);

        flightRepository.save(flight);

        return flight;
    }
}
