package com.homeaway.dropwizard.bundle.resilience4j.configuration.retry;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import io.dropwizard.util.Duration;
import io.github.resilience4j.retry.IntervalFunction;

@JsonTypeName("exponentialRandomBackoff")
public class ExponentialRandomBackoffFunctionFactory implements IntervalFunctionFactory {

    /**
     * Amount to multiply by at each iteration. Must be greater than 1. Default value is 1.5.
     */
    @JsonProperty
    @NotNull
    private double multiplier = IntervalFunction.DEFAULT_MULTIPLIER;

    /**
     * Randomization multiplier. Must be in range [0.0d, 1.0d). Default value is 0.5.
     */
    @JsonProperty
    @NotNull
    private double randomizationFactor = IntervalFunction.DEFAULT_RANDOMIZATION_FACTOR;

    /**
     * Initial interval. Minimum value is 10 milliseconds. Default is R4j default, currently 500ms.
     */
    @JsonProperty
    @NotNull
    private Duration initialInterval = Duration.milliseconds(IntervalFunction.DEFAULT_INITIAL_INTERVAL);

    @Override
    public IntervalFunction build() {
        return IntervalFunction.ofExponentialRandomBackoff(initialInterval.toMilliseconds(), multiplier, randomizationFactor);
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getRandomizationFactor() {
        return randomizationFactor;
    }

    public void setRandomizationFactor(double randomizationFactor) {
        this.randomizationFactor = randomizationFactor;
    }

    public Duration getInitialInterval() {
        return initialInterval;
    }

    public void setInitialInterval(Duration initialInterval) {
        this.initialInterval = initialInterval;
    }
}
