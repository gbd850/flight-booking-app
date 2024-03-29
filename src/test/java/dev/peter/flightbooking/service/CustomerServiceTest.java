package dev.peter.flightbooking.service;

import dev.peter.flightbooking.dto.CustomerRequestDto;
import dev.peter.flightbooking.dto.CustomerResponseDto;
import dev.peter.flightbooking.model.Customer;
import dev.peter.flightbooking.model.Role;
import dev.peter.flightbooking.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = NoOpPasswordEncoder.getInstance(); //dummy password encoder
        customerService = new CustomerService(customerRepository, passwordEncoder);
    }

    @Test
    void givenCustomer_whenGetCustomerById_thenReturnCustomerDto() {
        // given
        Integer id = 1;
        Customer customer = new Customer(id, "username1", passwordEncoder.encode("12345678"), Role.USER, new HashSet<>());

        given(customerRepository.findById(id)).willReturn(Optional.of(customer));
        // when
        CustomerResponseDto expected = customerService.getCustomerById(id);
        // then
        verify(customerRepository).findById(id);
        assertThat(expected).matches(c ->
                Objects.equals(c.id(), customer.getId())
                        && c.username().equals(customer.getUsername())
                        && c.password().equals(customer.getPassword())
                        && c.role().equals(customer.getRole().name())
                        && c.bookedFlights().equals(customer.getBookedFlights())
        );
    }

    @Test
    void givenNoCustomer_whenGetCustomerById_thenThrowException() {
        // given
        Integer id = 1;

        given(customerRepository.findById(id)).willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> customerService.getCustomerById(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Customer not found")
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    void givenCustomerDto_whenCreateCustomer_thenReturnCustomerDto() {
        // given
        CustomerRequestDto customerRequest = new CustomerRequestDto(null, "username1", "12345678", Role.USER.name());
        // when
        customerService.createCustomer(customerRequest);
        // then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer).matches(customer ->
                customer.getId() == null
                        && customer.getUsername().equals(customerRequest.username())
                        && customer.getPassword().equals(passwordEncoder.encode(customerRequest.password()))
                        && customer.getRole().name().equals(customerRequest.role())
                        && customer.getBookedFlights().equals(new HashSet<>())
        );
    }

    @Test
    void givenCustomerDtoWithInvalidRole_whenCreateCustomer_thenReturnCustomerDtoWithDefaultRole() {
        // given
        String invalidRoleName = "";
        CustomerRequestDto customerRequest = new CustomerRequestDto(null, "username1", "12345678", invalidRoleName);
        // when
        customerService.createCustomer(customerRequest);
        // then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer).matches(customer ->
                customer.getId() == null
                        && customer.getUsername().equals(customerRequest.username())
                        && customer.getPassword().equals(passwordEncoder.encode(customerRequest.password()))
                        && customer.getRole().equals(Role.USER)
                        && customer.getBookedFlights().equals(new HashSet<>())
        );
    }

    @Test
    void givenValidIdAndCustomerDto_whenEditCustomer_thenReturnUpdatedCustomerDto() {
        // given
        CustomerRequestDto customerRequest = new CustomerRequestDto(null, "username1", "12345678", Role.USER.name());

        Integer id = 1;
        Customer customerToEdit = new Customer(id, "username1", passwordEncoder.encode("12345678"), Role.USER, new HashSet<>());

        given(customerRepository.findById(id)).willReturn(Optional.of(customerToEdit));
        // when
        CustomerResponseDto customerResponseDto = customerService.editCustomer(id, customerRequest);
        // then
        verify(customerRepository).findById(id);

        assertThat(customerResponseDto).matches(customer ->
                customer.id().equals(customerToEdit.getId())
                        && customer.username().equals(customerRequest.username())
                        && customer.password().equals(passwordEncoder.encode(customerRequest.password()))
                        && customer.role().equals(customerRequest.role())
                        && customer.bookedFlights().equals(customerToEdit.getBookedFlights())
        );
    }

    @Test
    void givenInvalidIdAndCustomerDto_whenEditCustomer_thenThrowException() {
        // given
        CustomerRequestDto customerRequest = new CustomerRequestDto(null, "username1", "12345678", Role.USER.name());

        given(customerRepository.findById(anyInt())).willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> customerService.editCustomer(anyInt(), customerRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Customer not found")
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    void givenValidCustomerId_whenDeleteCustomer_thenInvokeDelete() {
        // given
        Integer id = 1;

        given(customerRepository.existsById(id)).willReturn(true);
        // when
        customerService.deleteCustomer(id);
        // then
        verify(customerRepository).deleteById(id);
    }

    @Test
    void givenInvalidCustomerId_whenDeleteCustomer_thenThrowException() {
        // given
        given(customerRepository.existsById(anyInt())).willReturn(false);
        // when
        // then
        assertThatThrownBy(() -> customerService.deleteCustomer(anyInt()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Customer not found")
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }
}