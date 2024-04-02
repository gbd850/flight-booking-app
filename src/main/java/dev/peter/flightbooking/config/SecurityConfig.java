package dev.peter.flightbooking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@Profile("!test")
@EnableMethodSecurity
public class SecurityConfig {
}
