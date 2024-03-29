package dev.peter.flightbooking.model;

import dev.peter.flightbooking.dto.FlightRequestDto;
import org.junit.jupiter.api.Test;
import org.mockito.internal.verification.Times;

import java.sql.Time;
import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FlightTest {

    @Test
    void givenValidFlightDto_whenUpdateEntityFromDto_thenUpdateEntity() {
        // given
        FlightRequestDto flightRequestDto = new FlightRequestDto("newName", 15.7, Timestamp.valueOf("2012-10-10 00:00:00"), Timestamp.valueOf("2012-11-11 00:00:00"), "newStartLocation", "newEndLocation", true);

        Flight flight = new Flight(1, "oldName", 10.5, Timestamp.valueOf("2011-10-10 00:00:00"), Timestamp.valueOf("2011-11-11 00:00:00"), "oldStartLocation", "oldEndLocation", true);
        // when
        flight.updateEntityFromDto(flightRequestDto);
        // then
        assertThat(flight).matches(f ->
                f.getName().equals(flightRequestDto.name())
                        && f.getPrice().equals(flightRequestDto.price())
                        && f.getStartDate().equals(flightRequestDto.startDate())
                        && f.getEndDate().equals(flightRequestDto.endDate())
                        && f.getStartLocation().equals(flightRequestDto.startLocation())
                        && f.getEndLocation().equals(flightRequestDto.endLocation())
                        && f.isAvailable() == flightRequestDto.isAvailable()
        );
    }

    @Test
    void givenValidPartialFlightDto_whenUpdateEntityFromDto_thenUpdateEntity() {
        // given
        FlightRequestDto flightRequestDto = new FlightRequestDto("newName", 15.7, Timestamp.valueOf("2012-10-10 00:00:00"), null, null, null, true);

        Timestamp oldEndDate = Timestamp.valueOf("2011-11-11 00:00:00");
        String oldStartLocation = "oldStartLocation";
        String oldEndLocation = "oldEndLocation";
        Flight flight = new Flight(1, "oldName", 10.5, Timestamp.valueOf("2011-10-10 00:00:00"), oldEndDate, oldStartLocation, oldEndLocation, true);
        // when
        flight.updateEntityFromDto(flightRequestDto);
        // then
        assertThat(flight).matches(f ->
                f.getName().equals(flightRequestDto.name())
                        && f.getPrice().equals(flightRequestDto.price())
                        && f.getStartDate().equals(flightRequestDto.startDate())
                        && f.getEndDate().equals(oldEndDate)
                        && f.getStartLocation().equals(oldStartLocation)
                        && f.getEndLocation().equals(oldEndLocation)
                        && f.isAvailable() == flightRequestDto.isAvailable()
        );
    }

    @Test
    void givenInvalidFlightDto_whenUpdateEntityFromDto_thenThrowException() {
        // given
        FlightRequestDto flightRequestDto = null;

        Timestamp oldEndDate = Timestamp.valueOf("2011-11-11 00:00:00");
        String oldStartLocation = "oldStartLocation";
        String oldEndLocation = "oldEndLocation";
        Flight flight = new Flight(1, "oldName", 10.5, Timestamp.valueOf("2011-10-10 00:00:00"), oldEndDate, oldStartLocation, oldEndLocation, true);
        // when
        // then
        assertThatThrownBy(() -> flight.updateEntityFromDto(flightRequestDto))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Invalid flight dto");
    }
}