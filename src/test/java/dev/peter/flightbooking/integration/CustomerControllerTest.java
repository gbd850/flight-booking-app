package dev.peter.flightbooking.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.peter.flightbooking.dto.CustomerRequestDto;
import dev.peter.flightbooking.model.Customer;
import dev.peter.flightbooking.model.Role;
import dev.peter.flightbooking.repository.CustomerRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

import java.util.HashSet;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerTest {

    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(
            "mysql:8.3.0"
    ).withReuse(true);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @LocalServerPort
    private Integer port;
    @Autowired
    private CustomerRepository customerRepository;

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        customerRepository.deleteAllInBatch();
    }

    @Test
    void givenValidCustomerId_whenGetCustomerById_thenReturnCustomer() {
        // given
        Customer customer = new Customer(null, "username", "password", Role.USER, new HashSet<>());

        Integer id = customerRepository.save(customer).getId();

        String customerJSON;
        try {
            customerJSON = objectMapper.writeValueAsString(customer);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // when
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/customer/" + id)

                // then
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("", equalTo(JsonPath.from(customerJSON).getMap("")));
    }

    @Test
    void givenInvalidCustomerId_whenGetCustomerById_thenReturnError() {
        // given
        int id = 1;

        // when
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/customer/" + id)

                // then
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo("Customer not found"))
                .body("status", equalTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void givenCustomer_whenCreateCustomer_thenStatus201() {
        // given
        CustomerRequestDto customerRequestDto = new CustomerRequestDto(null, "username", "password", Role.USER.name());

        // when
        given()
                .contentType(ContentType.JSON)
                .with()
                .body(customerRequestDto)
                .when()
                .post("/api/customer")

                // then
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void givenNoCustomer_whenCreateCustomer_thenReturnError() {
        // given
        // when
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/api/customer")

                // then
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void givenValidIdAndCustomerDto_whenEditCustomer_thenReturnUpdatedCustomer() {
        // given
        Customer customer = new Customer(null, "oldUsername", "oldPassword", Role.USER, new HashSet<>());
        Integer id = customerRepository.save(customer).getId();

        CustomerRequestDto customerRequestDto = new CustomerRequestDto(null, "username", "password", null);

        // when
        given()
                .contentType(ContentType.JSON)
                .with()
                .body(customerRequestDto)
                .when()
                .patch("/api/customer/" + id)

                // then
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("username", equalTo(customerRequestDto.username()))
                .body("password", equalTo(customerRequestDto.password()))
                .body("role", equalTo(customer.getRole().name()));
    }

    @Test
    void givenInvalidIdAndCustomerDto_whenEditCustomer_thenReturnError() {
        // given
        int id = 1;

        CustomerRequestDto customerRequestDto = new CustomerRequestDto(null, "username", "password", null);

        // when
        given()
                .contentType(ContentType.JSON)
                .with()
                .body(customerRequestDto)
                .when()
                .patch("/api/customer/" + id)

                // then
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo("Customer not found"))
                .body("status", equalTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void givenValidIdAndNoCustomerDto_whenEditCustomer_thenReturnError() {
        // given
        int id = 1;

        Customer customer = new Customer(id, "oldUsername", "oldPassword", Role.USER, new HashSet<>());
        customerRepository.save(customer);

        // when
        given()
                .contentType(ContentType.JSON)
                .with()
                .when()
                .patch("/api/customer/" + id)

                // then
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void givenInvalidIdAndNoCustomerDto_whenEditCustomer_thenReturnError() {
        // given
        int id = 1;

        // when
        given()
                .contentType(ContentType.JSON)
                .with()
                .when()
                .patch("/api/customer/" + id)

                // then
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void givenValidId_whenDeleteCustomer_thenStatus200() {
        // given
        Customer customer = new Customer(null, "username", "password", Role.USER, new HashSet<>());
        Integer id = customerRepository.save(customer).getId();

        // when
        given()
                .contentType(ContentType.JSON)
                .with()
                .when()
                .delete("/api/customer/" + id)

                // then
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void givenInvalidId_whenDeleteCustomer_thenReturnError() {
        // given
        int id = 1;

        // when
        given()
                .contentType(ContentType.JSON)
                .with()
                .when()
                .delete("/api/customer/" + id)

                // then
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo("Customer not found"))
                .body("status", equalTo(HttpStatus.NOT_FOUND.value()));
    }
}