package dev.peter.flightbooking.service;

import dev.peter.flightbooking.dto.*;
import dev.peter.flightbooking.model.Customer;
import dev.peter.flightbooking.model.Flight;
import dev.peter.flightbooking.model.Role;
import dev.peter.flightbooking.repository.CustomerRepository;
import dev.peter.flightbooking.repository.FlightRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@RateLimiter(name = "simpleRateLimit")
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final FlightRepository flightRepository;

    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("hasAuthority('SCOPE_user.read')")
    public CustomerResponseDto getCustomerById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found", new Throwable("Customer with id " + id + " does not exist")));
        return new CustomerResponseDto(
                customer.getId(),
                customer.getUsername(),
                customer.getPassword(),
                customer.getRole().name(),
                customer.getBookedFlights()
        );
    }

    @Transactional
//    @PreAuthorize("hasAuthority('SCOPE_user.write')")
    public CustomerResponseDto createCustomer(CustomerRequestDto customerRequestDto) {

        Role role;
        try {
            role = Role.valueOf(customerRequestDto.role());
        } catch (IllegalArgumentException | NullPointerException e) {
            role = Role.USER;
//            e.printStackTrace();
        }

        String password = passwordEncoder.encode(customerRequestDto.password());

        Customer customer = new Customer(
                null,
                customerRequestDto.username(),
                password,
                role,
                new HashSet<>()
        );
        customerRepository.save(customer);

        return new CustomerResponseDto(
                customer.getId(),
                customer.getUsername(),
                customer.getPassword(),
                customer.getRole().name(),
                customer.getBookedFlights()
        );
    }

    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_user.write')")
    public CustomerResponseDto editCustomer(Integer id, CustomerRequestDto customerRequestDto) {

        if (isNull(customerRequestDto)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request", new Throwable("Customer object body cannot be empty"));
        }

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found", new Throwable("Customer with id " + id + " does not exist")));

        customer.updateEntityFromDto(customerRequestDto);

        customerRepository.save(customer);

        return new CustomerResponseDto(
                customer.getId(),
                customer.getUsername(),
                customer.getPassword(),
                customer.getRole().name(),
                customer.getBookedFlights()
        );
    }

    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_user.write')")
    public void deleteCustomer(Integer id) {
        if (customerRepository.existsById(id)) {

            customerRepository.deleteById(id);

        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found", new Throwable("Customer with id " + id + " does not exist"));
    }

    public CustomerRoleResponseDto getCustomerRole(String username) {
        String role = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found", new Throwable("Customer with username " + username + " does not exist")))
                .getRole().name();
        return new CustomerRoleResponseDto(role);
    }

    @PreAuthorize("hasAuthority('SCOPE_user.read')")
    public CustomerBookingResponseDto getCustomerBookings(Integer id) {
        if (!customerRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found", new Throwable("Customer with id " + id + " does not exist"));
        }

        return new CustomerBookingResponseDto(customerRepository.findBookedFlightsByCustomerId(id));
    }

    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_user.write')")
    public CustomerBookingResponseDto bookCustomerFlight(Integer id, CustomerBookingRequestDto bookingRequest) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found", new Throwable("Customer with id " + id + " does not exist")));

        Flight flight = flightRepository.findById(bookingRequest.flightId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Flights not found", new Throwable("Could not find flights with id " + bookingRequest.flightId())));

        customer.getBookedFlights().add(flight);

        customerRepository.save(customer);

        return new CustomerBookingResponseDto(customer.getBookedFlights());
    }

    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_user.write')")
    public void deleteBookedCustomerFlight(Integer id, Integer flightId) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found", new Throwable("Customer with id " + id + " does not exist")));

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Flights not found", new Throwable("Could not find flights with id " + flightId)));

        customer.getBookedFlights().remove(flight);

        customerRepository.save(customer);
    }
}
