package com.fury.car_rental_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.jwt")
public class CustomJwtProperties {

    private String administrationIssuerUri;
    private String userIssuerUri;

    // Getters and Setters
    public String getAdministrationIssuerUri() {
        return administrationIssuerUri;
    }

    public void setAdministrationIssuerUri(String administrationIssuerUri) {
        this.administrationIssuerUri = administrationIssuerUri;
    }

    public String getUserIssuerUri() {
        return userIssuerUri;
    }

    public void setUserIssuerUri(String userIssuerUri) {
        this.userIssuerUri = userIssuerUri;
    }
}