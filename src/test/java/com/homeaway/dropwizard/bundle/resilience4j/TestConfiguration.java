package com.homeaway.dropwizard.bundle.resilience4j;

import com.homeaway.dropwizard.bundle.resilience4j.configuration.Resilience4jConfiguration;

import io.dropwizard.Configuration;

public class TestConfiguration extends Configuration {

    private Resilience4jConfiguration resilience4j;

    public Resilience4jConfiguration getResilience4j() {
        return resilience4j;
    }
}