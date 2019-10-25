package com.homeaway.dropwizard.bundle.resilience4j.configuration.retry;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import io.dropwizard.util.Duration;
import io.github.resilience4j.retry.IntervalFunction;

@JsonTypeName("randomized")
public class RandomizedIntervalFunctionFactory implements IntervalFunctionFactory {

    /**
     * Base interval before randomization is applied. Minimum value is 10 milliseconds. Default is R4j default, currently 500ms.
     */
    @JsonProperty
    @NotNull
    private Duration initialInterval = Duration.milliseconds(IntervalFunction.DEFAULT_INITIAL_INTERVAL);

    /**
     * Randomization multiplier. Must be in range [0.0d, 1.0d)
     */
    @JsonProperty
    @NotNull
    private double randomizationFactor = IntervalFunction.DEFAULT_RANDOMIZATION_FACTOR;

    @Override
    public IntervalFunction build() {
       return IntervalFunction.ofRandomized(initialInterval.toMilliseconds(), randomizationFactor);
    }

    public Duration getInitialInterval() {
        return initialInterval;
    }

    public void setInitialInterval(Duration initialInterval) {
        this.initialInterval = initialInterval;
    }

    public double getRandomizationFactor() {
        return randomizationFactor;
    }

    public void setRandomizationFactor(double randomizationFactor) {
        this.randomizationFactor = randomizationFactor;
    }
}
