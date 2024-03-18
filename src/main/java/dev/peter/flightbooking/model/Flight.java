package dev.peter.flightbooking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.peter.flightbooking.dto.FlightRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Flight implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(nullable = false)
    private String startLocation;

    private String endLocation;

    @Column(nullable = false)
    private boolean isAvailable = true;

    public void updateEntityFromDto(FlightRequestDto flightRequestDto) {
        this.name = Objects.requireNonNullElse(flightRequestDto.name(), this.name);
        this.startDate = Objects.requireNonNullElse(flightRequestDto.startDate(), this.startDate);
        this.endDate = Objects.requireNonNullElse(flightRequestDto.endDate(), this.endDate);
        this.startLocation = Objects.requireNonNullElse(flightRequestDto.startLocation(), this.startLocation);
        this.endLocation = Objects.requireNonNullElse(flightRequestDto.endLocation(), this.endLocation);
        this.price = Objects.requireNonNullElse(flightRequestDto.price(), this.price);
    }
}