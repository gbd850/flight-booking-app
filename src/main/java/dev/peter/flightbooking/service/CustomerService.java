package dev.peter.flightbooking.service;

import dev.peter.flightbooking.dto.CustomerRequestDto;
import dev.peter.flightbooking.dto.CustomerResponseDto;
import dev.peter.flightbooking.model.Customer;
import dev.peter.flightbooking.model.Role;
import dev.peter.flightbooking.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    public CustomerResponseDto getCustomerById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        return new CustomerResponseDto(
                customer.getId(),
                customer.getUsername(),
                customer.getPassword(),
                customer.getRole().name(),
                customer.getBookedFlights()
        );
    }

    public CustomerResponseDto createCustomer(CustomerRequestDto customerRequestDto) {

        Role role;
        try {
            role = Role.valueOf(customerRequestDto.role());
        } catch (IllegalArgumentException e) {
            role = Role.USER;
            e.printStackTrace();
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

    public CustomerResponseDto editCustomer(Integer id, CustomerRequestDto customerRequestDto) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

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

    public void deleteCustomer(Integer id) {
        if (customerRepository.existsById(id)) {

            customerRepository.deleteById(id);

        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
    }

}
