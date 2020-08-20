package com.expediagroup.dropwizard.resilience4j;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

public class TestApplication extends Application<TestConfiguration> {

    private List<Pair<String, CircuitBreakerConfig.Builder>> breakersSeenInConfiguration = new ArrayList<>();

    private List<Pair<String, RetryConfig.Builder>> retryersSeenInConfiguration = new ArrayList<>();

    private List<Pair<String, TimeLimiterConfig.Builder>> timeLimitersSeenInConfiguration = new ArrayList<>();

    @Override
    public void initialize(Bootstrap<TestConfiguration> bootstrap) {
        Resilience4jBundle<TestConfiguration> bundle =
            new Resilience4jBundle<>(TestConfiguration::getResilience4j,
                this::addBreakerConfigurationToTestList,
                this::addRetryConfigurationToTestList,
                this::addTimeLimiterConfigurationToTestList);

        bootstrap.addBundle(bundle);
    }

    @Override
    public void run(TestConfiguration configuration, Environment environment) {
    }

    private void addBreakerConfigurationToTestList(String key, CircuitBreakerConfig.Builder builder) {
        breakersSeenInConfiguration.add(Pair.of(key, builder));
    }

    private void addRetryConfigurationToTestList(String key, RetryConfig.Builder builder) {
        retryersSeenInConfiguration.add(Pair.of(key, builder));
    }

    private void addTimeLimiterConfigurationToTestList(String key, TimeLimiterConfig.Builder builder) {
        timeLimitersSeenInConfiguration.add(Pair.of(key, builder));
    }

    public List<Pair<String, CircuitBreakerConfig.Builder>> getBreakersSeenInConfiguration() {
        return breakersSeenInConfiguration;
    }

    public List<Pair<String, RetryConfig.Builder>> getRetryersSeenInConfiguration() {
        return retryersSeenInConfiguration;
    }

    public List<Pair<String, TimeLimiterConfig.Builder>> getTimeLimitersSeenInConfiguration() {
        return timeLimitersSeenInConfiguration;
    }
}
