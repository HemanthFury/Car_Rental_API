package com.fury.car_rental_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class JwtConverterConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Extract roles from the "realm_access" claim
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess == null) {
                return List.of(); // Return empty list if "realm_access" is not present
            }

            List<String> roles = (List<String>) realmAccess.get("roles");
            if (roles == null) {
                return List.of(); // Return empty list if no roles are found
            }

            // Map roles to Spring Security authorities with ROLE_ prefix
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Prefix with ROLE_
                    .collect(Collectors.toList());
        });
        return converter;
    }
}