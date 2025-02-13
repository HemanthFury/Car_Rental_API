package com.fury.car_rental_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;

@Configuration
public class JwtDecoderConfig {

    private final CustomJwtProperties customJwtProperties;

    public JwtDecoderConfig(CustomJwtProperties customJwtProperties) {
        this.customJwtProperties = customJwtProperties;
    }

    @Bean
    public JwtDecoder administrationJwtDecoder() {
        return JwtDecoders.fromIssuerLocation(customJwtProperties.getAdministrationIssuerUri());
    }

    @Bean
    public JwtDecoder userJwtDecoder() {
        return JwtDecoders.fromIssuerLocation(customJwtProperties.getUserIssuerUri());
    }
}