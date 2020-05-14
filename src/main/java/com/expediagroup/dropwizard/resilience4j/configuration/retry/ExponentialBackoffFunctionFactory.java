package com.expediagroup.dropwizard.resilience4j.configuration.retry;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import io.dropwizard.util.Duration;
import io.github.resilience4j.core.IntervalFunction;

@JsonTypeName("exponentialBackoff")
public class ExponentialBackoffFunctionFactory implements IntervalFunctionFactory {

    /**
     * Amount to multiply by at each iteration. Must be greater than 1.  Default value is 1.5.
     */
    @JsonProperty
    @NotNull
    private double multiplier = IntervalFunction.DEFAULT_MULTIPLIER;

    /**
     * Initial interval. Minimum value is 10 milliseconds. Default is R4j default, currently 500ms.
     */
    @JsonProperty
    @NotNull
    private Duration initialInterval = Duration.milliseconds(IntervalFunction.DEFAULT_INITIAL_INTERVAL);

    @Override
    public IntervalFunction build() {
        return IntervalFunction.ofExponentialBackoff(initialInterval.toMilliseconds(), multiplier);
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public Duration getInitialInterval() {
        return initialInterval;
    }

    public void setInitialInterval(Duration initialInterval) {
        this.initialInterval = initialInterval;
    }
}
