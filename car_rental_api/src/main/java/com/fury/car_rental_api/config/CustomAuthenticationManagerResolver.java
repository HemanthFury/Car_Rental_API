package com.fury.car_rental_api.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationManagerResolver implements AuthenticationManagerResolver<HttpServletRequest> {

    private final Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();

    public CustomAuthenticationManagerResolver(JwtDecoder administrationJwtDecoder, JwtDecoder userJwtDecoder, JwtAuthenticationConverter jwtAuthenticationConverter) {
        // Create AuthenticationManager for Administration realm
        JwtAuthenticationProvider administrationProvider = new JwtAuthenticationProvider(administrationJwtDecoder);
        administrationProvider.setJwtAuthenticationConverter(jwtAuthenticationConverter); // Set the converter
        authenticationManagers.put("Administration", administrationProvider::authenticate);

        // Create AuthenticationManager for User realm
        JwtAuthenticationProvider userProvider = new JwtAuthenticationProvider(userJwtDecoder);
        userProvider.setJwtAuthenticationConverter(jwtAuthenticationConverter); // Set the converter
        authenticationManagers.put("User", userProvider::authenticate);
    }

    @Override
    public AuthenticationManager resolve(HttpServletRequest request) {
        // Determine the realm based on the request path
        String realm = determineRealm(request);
        return authenticationManagers.get(realm);
    }

    private String determineRealm(HttpServletRequest request) {
        String requestPath = request.getRequestURI();

        // Assign paths starting with /admin to the Administration realm
        if (requestPath.startsWith("/api/v1/users/register")) {
            return "Administration";
        }
        // Assign paths starting with /api/v1/users to the User realm
        else if (requestPath.startsWith("/api/v1/users/getUser")) {
            return "User";
        }
        // Assign paths starting with /user to the User realm
        else if (requestPath.startsWith("/user")) {
            return "User";
        }
        // Default to the User realm for all other paths
        else {
            return "User"; // or throw an exception if you want to enforce strict path-realm mapping
        }
    }
}