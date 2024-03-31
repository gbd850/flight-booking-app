package dev.peter.flightbooking.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.peter.flightbooking.dto.FlightRequestDto;
import dev.peter.flightbooking.dto.FlightResponseDto;
import dev.peter.flightbooking.model.Flight;
import dev.peter.flightbooking.repository.FlightRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FlightControllerTest {

    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(
            "mysql:8.3.0"
    ).withReuse(true);

    static GenericContainer<?> redis = new GenericContainer<>(
            "redis:alpine3.19"
    ).withExposedPorts(6379);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @LocalServerPort
    private Integer port;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeAll
    static void beforeAll() {
        redis.start();
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
        redis.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        flightRepository.deleteAllInBatch();
        // flush cache before each test
        cacheManager.getCacheNames().forEach(cache -> Objects.requireNonNull(cacheManager.getCache(cache)).clear());
    }

    @Test
    void givenValidStartLocation_whenGetFLightsByStartLocation_thenReturnFlightsList() {
        // given
        String startLocation = "Location1";
        List<Flight> flights = List.of(
                new Flight(null, "Flight1", 10.5, Timestamp.valueOf(LocalDateTime.parse("2010-10-10T00:00:00")), null, startLocation, null, true),
                new Flight(null, "Flight2", 15.5, Timestamp.valueOf(LocalDateTime.parse("2010-10-10T00:00:00")), null, startLocation, null, true)
        );

        flightRepository.saveAll(flights);

        // when
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/flight?startLocation=" + startLocation)

                // then
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(".", hasSize(2))
                .body(".", everyItem(hasEntry("startLocation", startLocation)));
    }

    @Test
    void givenValidStartLocationAndFiltered_whenGetFLightsByStartLocation_thenReturnFilteredFlightsList() {
        // given
        String startLocation = "Location1";
        List<Flight> flights = List.of(
                new Flight(null, "Flight1", 10.5, Timestamp.valueOf(LocalDateTime.parse("2010-10-10T00:00:00")), null, startLocation, null, true),
                new Flight(null, "Flight2", 15.5, Timestamp.valueOf(LocalDateTime.parse("2010-10-10T00:00:00")), null, startLocation, null, false),
                new Flight(null, "Flight3", 12.5, Timestamp.valueOf(LocalDateTime.parse("2010-10-10T00:00:00")), null, startLocation, null, true)
        );

        flightRepository.saveAll(flights);

        // when
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/flight?startLocation=" + startLocation + "&filterUnavailable=true")

                // then
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(".", hasSize(2))
                .body(".", everyItem(hasEntry("startLocation", startLocation)))
                .body(".", everyItem(hasEntry("isAvailable", true)));
    }

    @Test
    void givenInvalidStartLocation_whenGetFLightsByStartLocation_thenReturnError() {
        // given
        String startLocation = "Location1";

        // when
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/flight?startLocation=" + startLocation)

                // then
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo("Flights not found"))
                .body("status", equalTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void givenValidEndLocation_whenGetFLightsByEndLocation_thenReturnFlightsList() {
        // given
        String endLocation = "Location2";
        List<Flight> flights = List.of(
                new Flight(null, "Flight1", 10.5, Timestamp.valueOf(LocalDateTime.parse("2010-10-10T00:00:00")), null, "Location1", endLocation, true),
                new Flight(null, "Flight2", 15.5, Timestamp.valueOf(LocalDateTime.parse("2010-10-10T00:00:00")), null, "Location2", endLocation, false),
                new Flight(null, "Flight3", 12.5, Timestamp.valueOf(LocalDateTime.parse("2010-10-10T00:00:00")), null, "Location3", endLocation, true)
        );

        flightRepository.saveAll(flights);

        // when
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/flight?endLocation=" + endLocation)

                // then
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(".", hasSize(3))
                .body(".", everyItem(hasEntry("endLocation", endLocation)));
    }

    @Test
    void givenValidEndLocationAndFiltered_whenGetFLightsByEndLocation_thenReturnFilteredFlightsList() {
        // given
        String endLocation = "Location2";
        List<Flight> flights = List.of(
                new Flight(null, "Flight1", 10.5, Timestamp.valueOf(LocalDateTime.parse("2010-10-10T00:00:00")), null, "Location1", endLocation, true),
                new Flight(null, "Flight2", 15.5, Timestamp.valueOf(LocalDateTime.parse("2010-10-10T00:00:00")), null, "Location2", endLocation, false),
                new Flight(null, "Flight3", 12.5, Timestamp.valueOf(LocalDateTime.parse("2010-10-10T00:00:00")), null, "Location3", endLocation, true)
        );

        flightRepository.saveAll(flights);

        // when
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/flight?endLocation=" + endLocation + "&filterUnavailable=true")

                // then
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(".", hasSize(2))
                .body(".", everyItem(hasEntry("endLocation", endLocation)))
                .body(".", everyItem(hasEntry("isAvailable", true)));
    }

    @Test
    void givenInvalidEndLocation_whenGetFLightsByEndLocation_thenReturnError() {
        // given
        String endLocation = "Location2";

        // when
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/flight?endLocation=" + endLocation)

                // then
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo("Flights not found"))
                .body("status", equalTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void givenValidStartDateAndValidEndDate_whenGetFLightsByTimeFrame_thenReturnFlightsList() {
        // given
        String startDate = "2010-10-10";
        String endDate = "2011-11-11";
        LocalDateTime startDateTime = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();


        List<Flight> flights = List.of(
                new Flight(null, "Flight1", 10.5, Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), "Location1", null, true),
                new Flight(null, "Flight2", 15.5, Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), "Location2", null, false),
                new Flight(null, "Flight3", 12.5, Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), "Location3", null, true)
        );

        flightRepository.saveAll(flights);

        // when
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/flight?startDate=" + startDate + "&endDate=" + endDate)

                // then
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(".", hasSize(3))
                .extract()
                .jsonPath()
                .getList(".", FlightResponseDto.class)
                .forEach(flight -> assertAll(
                        () -> assertThat(flight.startDate().toLocalDateTime()).isEqualTo(startDateTime),
                        () -> assertThat(flight.endDate().toLocalDateTime()).isEqualTo(endDateTime)
                ));
    }

    @Test
    void givenValidStartDateAndValidEndDateAndFiltered_whenGetFLightsByTimeFrame_thenReturnFilteredFlightsList() {
        // given
        String startDate = "2010-10-10";
        String endDate = "2011-11-11";
        LocalDateTime startDateTime = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();


        List<Flight> flights = List.of(
                new Flight(null, "Flight1", 10.5, Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), "Location1", null, true),
                new Flight(null, "Flight2", 15.5, Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), "Location2", null, false),
                new Flight(null, "Flight3", 12.5, Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), "Location3", null, true)
        );

        flightRepository.saveAll(flights);

        // when
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/flight?startDate=" + startDate + "&endDate=" + endDate + "&filterUnavailable=true")

                // then
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(".", hasSize(2))
                .body(".", everyItem(hasEntry("isAvailable", true)))
                .extract()
                .jsonPath()
                .getList(".", FlightResponseDto.class)
                .forEach(flight -> assertAll(
                        () -> assertThat(flight.startDate().toLocalDateTime()).isEqualTo(startDateTime),
                        () -> assertThat(flight.endDate().toLocalDateTime()).isEqualTo(endDateTime)
                ));
    }

    @Test
    void givenInvalidStartDateAndValidEndDate_whenGetFLightsByTimeFrame_thenReturnError() {
        // given
        String startDate = "2010-10-10";
        String endDate = "2011-11-11";
        LocalDateTime startDateTime = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();


        List<Flight> flights = List.of(
                new Flight(null, "Flight1", 10.5, Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), "Location1", null, true),
                new Flight(null, "Flight2", 15.5, Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), "Location2", null, false),
                new Flight(null, "Flight3", 12.5, Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), "Location3", null, true)
        );

        flightRepository.saveAll(flights);

        String invalidStartDate = "";

        // when
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/flight?startDate=" + invalidStartDate + "&endDate=" + endDate)

                // then
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo("Flights not found"))
                .body("status", equalTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void givenValidStartDateAndInvalidEndDate_whenGetFLightsByTimeFrame_thenReturnError() {
        // given
        String startDate = "2010-10-10";
        String endDate = "2011-11-11";
        LocalDateTime startDateTime = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();


        List<Flight> flights = List.of(
                new Flight(null, "Flight1", 10.5, Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), "Location1", null, true),
                new Flight(null, "Flight2", 15.5, Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), "Location2", null, false),
                new Flight(null, "Flight3", 12.5, Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), "Location3", null, true)
        );

        flightRepository.saveAll(flights);

        String invalidEndDate = "";

        // when
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/flight?startDate=" + startDate + "&endDate=" + invalidEndDate)

                // then
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo("Flights not found"))
                .body("status", equalTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void givenInvalidStartDateAndInvalidEndDate_whenGetFLightsByTimeFrame_thenReturnError() {
        // given
        String startDate = "2010-10-10";
        String endDate = "2011-11-11";
        LocalDateTime startDateTime = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();


        List<Flight> flights = List.of(
                new Flight(null, "Flight1", 10.5, Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), "Location1", null, true),
                new Flight(null, "Flight2", 15.5, Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), "Location2", null, false),
                new Flight(null, "Flight3", 12.5, Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), "Location3", null, true)
        );

        flightRepository.saveAll(flights);

        String invalidStartDate = "";
        String invalidEndDate = "";

        // when
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/flight?startDate=" + invalidStartDate + "&endDate=" + invalidEndDate)

                // then
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo("Flights not found"))
                .body("status", equalTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void givenFlight_whenCreateFlight_thenStatus201() {
        // given
        FlightRequestDto flightRequestDto = new FlightRequestDto("Flight1", 10.5, Timestamp.valueOf(LocalDateTime.parse("2010-10-10T00:00:00")), Timestamp.valueOf(LocalDateTime.parse("2010-10-10T00:00:00")), "Location1", null, true);

        // when
        given()
                .contentType(ContentType.JSON)
                .with()
                .body(flightRequestDto)
                .when()
                .post("/api/flight")

                // then
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void givenNoFlight_whenCreateFlight_thenReturnError() {
        // given
        // when
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/api/flight")

                // then
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void givenValidId_whenDeleteFlight_thenStatus200() {
        // given
        Flight flight = new Flight(null, "Flight1", 10.5, Timestamp.valueOf(LocalDateTime.parse("2010-10-10T00:00:00")), Timestamp.valueOf(LocalDateTime.parse("2010-10-10T00:00:00")), "Location1", null, true);

        Integer id = flightRepository.save(flight).getId();

        // when
        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/flight/" + id)

                // then
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void givenInvalidId_whenDeleteFlight_thenReturnError() {
        // given
        int id = 1;

        // when
        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/flight/" + id)

                // then
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo("Flight not found"))
                .body("status", equalTo(HttpStatus.NOT_FOUND.value()));
    }
}