package dev.peter.flightbooking.model;

import dev.peter.flightbooking.dto.CustomerRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @ManyToMany
    @JoinTable(
            name = "customers_flights",
            joinColumns = @JoinColumn(name = "flight_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private Set<Flight> bookedFlights;

    public void updateEntityFromDto(CustomerRequestDto customerRequestDto) {
        this.username = Objects.requireNonNullElse(customerRequestDto.username(), this.username);
        this.password = Objects.requireNonNullElse(customerRequestDto.password(), this.password);
        try {
            this.role = Objects.requireNonNullElse(Role.valueOf(customerRequestDto.role()), this.role);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

}
