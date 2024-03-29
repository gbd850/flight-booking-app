package dev.peter.flightbooking.service;

import dev.peter.flightbooking.dto.FlightRequestDto;
import dev.peter.flightbooking.dto.FlightResponseDto;
import dev.peter.flightbooking.model.Flight;
import dev.peter.flightbooking.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    private FlightService flightService;

    @Mock
    private FlightRepository flightRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager = new NoOpCacheManager(); //dummy cache manager
        flightService = new FlightService(flightRepository, cacheManager);
    }

    @Test
    void givenValidStartLocationAndNotFiltered_whenGetFLightsByStartLocation_thenReturnFlightsDtoList() {
        // given
        String startLocation = "Location1";

        List<Flight> flights = List.of(
                new Flight(1, "Flight1", 10.5, null, null, startLocation, null, true),
                new Flight(2, "Flight2", 10.5, null, null, startLocation, null, false)
        );

        given(flightRepository.findByStartLocation(startLocation)).willReturn(flights);
        // when
        List<FlightResponseDto> expected = flightService.getFLightsByStartLocation(startLocation, false);
        // then
        verify(flightRepository).findByStartLocation(startLocation);

        // Convert output list to map in order to execute assertions with indexes
        Map<Integer, FlightResponseDto> flightMap = IntStream.range(0, expected.size())
                .boxed()
                .collect(toMap(i -> i, expected::get));

        assertThat(flightMap)
                .hasSize(2)
                .allSatisfy((index, flight) -> assertThat(flight)
                        .matches(f ->
                                f.id().equals(flights.get(index).getId())
                                && f.name().equals(flights.get(index).getName())
                                && f.startLocation().equals(flights.get(index).getStartLocation())
                                && f.isAvailable() == flights.get(index).isAvailable()
                        )
                );
    }

    @Test
    void givenValidStartLocationAndFiltered_whenGetFLightsByStartLocation_thenReturnFlightsDtoList() {
        // given
        String startLocation = "Location1";

        List<Flight> flights = List.of(
                new Flight(1, "Flight1", 10.5, null, null, startLocation, null, true),
                new Flight(2, "Flight2", 10.5, null, null, startLocation, null, false)
        );

        given(flightRepository.findByStartLocation(startLocation)).willReturn(flights);
        // when
        List<FlightResponseDto> expected = flightService.getFLightsByStartLocation(startLocation, true);
        // then
        verify(flightRepository).findByStartLocation(startLocation);

        assertThat(expected)
                .hasSize(1)
                .allMatch(flightResponseDto ->
                        flightResponseDto.id().equals(flights.get(0).getId())
                        && flightResponseDto.name().equals(flights.get(0).getName())
                        && flightResponseDto.startLocation().equals(flights.get(0).getStartLocation())
                        && flightResponseDto.isAvailable()
                );
    }

    @Test
    void givenInvalidStartLocation_whenGetFLightsByStartLocation_thenThrowException() {
        // given
        given(flightRepository.findByStartLocation(anyString())).willReturn(List.of());
        // when
        // then
        assertThatThrownBy(() -> flightService.getFLightsByStartLocation(anyString(), false))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Could not find flights matching start location")
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    void givenValidEndLocationAndNotFiltered_whenGetFLightsByEndLocation_thenReturnFlightsDtoList() {
        // given
        String endLocation = "Location2";

        List<Flight> flights = List.of(
                new Flight(1, "Flight1", 10.5, null, null, "Location1", endLocation, true),
                new Flight(2, "Flight2", 10.5, null, null, "Location1", endLocation, false)
        );

        given(flightRepository.findByEndLocation(endLocation)).willReturn(flights);
        // when
        List<FlightResponseDto> expected = flightService.getFLightsByEndLocation(endLocation, false);
        // then
        verify(flightRepository).findByEndLocation(endLocation);

        // Convert output list to map in order to execute assertions with indexes
        Map<Integer, FlightResponseDto> flightMap = IntStream.range(0, expected.size())
                .boxed()
                .collect(toMap(i -> i, expected::get));

        assertThat(flightMap)
                .hasSize(2)
                .allSatisfy((index, flight) -> assertThat(flight)
                        .matches(f ->
                                f.id().equals(flights.get(index).getId())
                                        && f.name().equals(flights.get(index).getName())
                                        && f.startLocation().equals(flights.get(index).getStartLocation())
                                        && f.endLocation().equals(flights.get(index).getEndLocation())
                                        && f.isAvailable() == flights.get(index).isAvailable()
                        )
                );
    }

    @Test
    void givenValidEndLocationAndFiltered_whenGetFLightsByEndLocation_thenReturnFlightsDtoList() {
        // given
        String endLocation = "Location2";

        List<Flight> flights = List.of(
                new Flight(1, "Flight1", 10.5, null, null, "Location1", endLocation, true),
                new Flight(2, "Flight2", 10.5, null, null, "Location1", endLocation, false)
        );

        given(flightRepository.findByEndLocation(endLocation)).willReturn(flights);
        // when
        List<FlightResponseDto> expected = flightService.getFLightsByEndLocation(endLocation, true);
        // then
        verify(flightRepository).findByEndLocation(endLocation);

        assertThat(expected)
                .hasSize(1)
                .allMatch(flightResponseDto ->
                        flightResponseDto.id().equals(flights.get(0).getId())
                                && flightResponseDto.name().equals(flights.get(0).getName())
                                && flightResponseDto.startLocation().equals(flights.get(0).getStartLocation())
                                && flightResponseDto.endLocation().equals(flights.get(0).getEndLocation())
                                && flightResponseDto.isAvailable()
                );
    }

    @Test
    void givenInvalidEndLocation_whenGetFLightsByEndLocation_thenThrowException() {
        // given
        given(flightRepository.findByEndLocation(anyString())).willReturn(List.of());
        // when
        // then
        assertThatThrownBy(() -> flightService.getFLightsByEndLocation(anyString(), false))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Could not find flights matching end location")
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    void givenValidStartDateAndValidEndDateAndNotFiltered_whenGetFLightsByTimeFrame_thenReturnFlightsDtoList() {
        // given
        String startDate = "2011-10-10";
        String endDate = "2011-11-11";

        List<Flight> flights = List.of(
                new Flight(1, "Flight1", 10.5, Timestamp.valueOf(startDate + " 00:00:00"), Timestamp.valueOf(endDate + " 00:00:00"), "Location1", null, true),
                new Flight(2, "Flight2", 10.5, Timestamp.valueOf(startDate + " 00:00:00"), Timestamp.valueOf(endDate + " 00:00:00"), "Location1", null, false)
        );

        given(flightRepository.findByStartDateAndEndDate(startDate, endDate)).willReturn(flights);
        // when
        List<FlightResponseDto> expected = flightService.getFLightsByTimeFrame(startDate, endDate, false);
        // then
        verify(flightRepository).findByStartDateAndEndDate(startDate, endDate);

        // Convert output list to map in order to execute assertions with indexes
        Map<Integer, FlightResponseDto> flightMap = IntStream.range(0, expected.size())
                .boxed()
                .collect(toMap(i -> i, expected::get));

        assertThat(flightMap)
                .hasSize(2)
                .allSatisfy((index, flight) -> assertThat(flight)
                        .matches(f ->
                                f.id().equals(flights.get(index).getId())
                                        && f.name().equals(flights.get(index).getName())
                                        && f.startLocation().equals(flights.get(index).getStartLocation())
                                        && f.startDate().equals(flights.get(index).getStartDate())
                                        && f.endDate().equals(flights.get(index).getEndDate())
                                        && f.isAvailable() == flights.get(index).isAvailable()
                        )
                );
    }

    @Test
    void givenValidStartDateAndValidEndDateAndFiltered_whenGetFLightsByTimeFrame_thenReturnFlightsDtoList() {
        // given
        String startDate = "2011-10-10";
        String endDate = "2011-11-11";

        List<Flight> flights = List.of(
                new Flight(1, "Flight1", 10.5, Timestamp.valueOf(startDate + " 00:00:00"), Timestamp.valueOf(endDate + " 00:00:00"), "Location1", null, true),
                new Flight(2, "Flight2", 10.5, Timestamp.valueOf(startDate + " 00:00:00"), Timestamp.valueOf(endDate + " 00:00:00"), "Location1", null, false)
        );

        given(flightRepository.findByStartDateAndEndDate(startDate, endDate)).willReturn(flights);
        // when
        List<FlightResponseDto> expected = flightService.getFLightsByTimeFrame(startDate, endDate, true);
        // then
        verify(flightRepository).findByStartDateAndEndDate(startDate, endDate);

        assertThat(expected)
                .hasSize(1)
                .allMatch(flightResponseDto ->
                        flightResponseDto.id().equals(flights.get(0).getId())
                                && flightResponseDto.name().equals(flights.get(0).getName())
                                && flightResponseDto.startLocation().equals(flights.get(0).getStartLocation())
                                && flightResponseDto.startDate().equals(flights.get(0).getStartDate())
                                && flightResponseDto.endDate().equals(flights.get(0).getEndDate())
                                && flightResponseDto.isAvailable()
                );
    }

    @Test
    void givenInvalidStartDateAndValidEndDate_whenGetFLightsByTimeFrame_thenThrowException() {
        // given
        String startDate = "";
        String endDate = "2011-11-11";

        given(flightRepository.findByStartDateAndEndDate(startDate, endDate)).willReturn(List.of());
        // when
        // then
        assertThatThrownBy(() -> flightService.getFLightsByTimeFrame(startDate, endDate, false))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Could not find flights matching time frame")
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    void givenValidStartDateAndInvalidEndDate_whenGetFLightsByTimeFrame_thenThrowException() {
        // given
        String startDate = "2011-10-10";
        String endDate = "";

        given(flightRepository.findByStartDateAndEndDate(startDate, endDate)).willReturn(List.of());
        // when
        // then
        assertThatThrownBy(() -> flightService.getFLightsByTimeFrame(startDate, endDate, false))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Could not find flights matching time frame")
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    void givenInvalidStartDateAndInvalidEndDate_whenGetFLightsByTimeFrame_thenThrowException() {
        // given
        String startDate = "";
        String endDate = "";

        given(flightRepository.findByStartDateAndEndDate(startDate, endDate)).willReturn(List.of());
        // when
        // then
        assertThatThrownBy(() -> flightService.getFLightsByTimeFrame(startDate, endDate, false))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Could not find flights matching time frame")
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    void givenFlightDto_whenCreateFlight_thenReturnFlightDto() {
        // given
        FlightRequestDto flightRequestDto = new FlightRequestDto("Flight1", 10.5, Timestamp.valueOf("2011-10-10 00:00:00"), null, "Location1", null, true);
        // when
        flightService.createFlight(flightRequestDto);
        // then
        ArgumentCaptor<Flight> flightArgumentCaptor = ArgumentCaptor.forClass(Flight.class);
        verify(flightRepository).save(flightArgumentCaptor.capture());

        Flight capturedFlight = flightArgumentCaptor.getValue();

        assertThat(capturedFlight).matches(flight ->
                flight.getName().equals(flightRequestDto.name())
                && flight.getPrice().equals(flightRequestDto.price())
                && flight.getStartDate().equals(flightRequestDto.startDate())
                && flight.getStartLocation().equals(flightRequestDto.startLocation())
                && flight.isAvailable() == flightRequestDto.isAvailable()
                );
    }

    @Test
    void givenValidId_whenDeleteFlight_thenInvokeDelete() {
        // given
        Integer id = 1;
        Flight flightToDelete = new Flight(id, "Flight1", 10.5, Timestamp.valueOf("2011-10-10 00:00:00"), Timestamp.valueOf("2011-10-10 00:00:00"), "", null, true);
        given(flightRepository.findById(id)).willReturn(Optional.of(flightToDelete));
        // when
        flightService.deleteFlight(id);
        // then
        verify(flightRepository).delete(flightToDelete);
    }

    @Test
    void givenInvalidId_whenDeleteFlight_thenThrowException() {
        // given
        given(flightRepository.findById(anyInt())).willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> flightService.deleteFlight(anyInt()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Flight not found")
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    void givenValidIdAndFlightDto_whenEditFlight_thenReturnUpdatedFlightDto() {
        // given
        Integer id = 1;
        FlightRequestDto flightRequestDto = new FlightRequestDto("Flight1", 10.5, Timestamp.valueOf("2011-10-10 00:00:00"), Timestamp.valueOf("2011-10-10 00:00:00"), "Location1", "Location2", true);
        Flight flightToEdit = new Flight(id, null, null, null, null, null, null, true);

        given(flightRepository.findById(id)).willReturn(Optional.of(flightToEdit));
        // when
        FlightResponseDto updatedFlight = flightService.editFlight(id, flightRequestDto);
        // then
        verify(flightRepository).findById(id);

        assertThat(updatedFlight).matches(flight ->
                flight.id().equals(flightToEdit.getId())
                && flight.name().equals(flightRequestDto.name())
                && flight.price().equals(flightRequestDto.price())
                && flight.startDate().equals(flightRequestDto.startDate())
                && flight.endDate().equals(flightRequestDto.endDate())
                && flight.startLocation().equals(flightRequestDto.startLocation())
                && flight.endLocation().equals(flightRequestDto.endLocation())
                && flight.isAvailable() == flightRequestDto.isAvailable()
                );
    }

    @Test
    void givenInvalidId_whenEditFlight_thenThrowException() {
        // given
        FlightRequestDto flightRequestDto = null;

        given(flightRepository.findById(anyInt())).willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> flightService.editFlight(anyInt(), flightRequestDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Flight not found")
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }
}