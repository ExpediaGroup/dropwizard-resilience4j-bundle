/*
Copyright 2018 Expedia Group, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.homeaway.dropwizard.bundle.resilience4j;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.homeaway.dropwizard.bundle.resilience4j.configuration.CircuitBreakerConfiguration;
import com.homeaway.dropwizard.bundle.resilience4j.configuration.Resilience4jConfiguration;
import com.homeaway.dropwizard.bundle.resilience4j.configuration.retry.RetryConfiguration;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.internal.InMemoryCircuitBreakerRegistry;
import io.github.resilience4j.core.lang.NonNull;
import io.github.resilience4j.metrics.CircuitBreakerMetrics;
import io.github.resilience4j.metrics.RetryMetrics;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.internal.InMemoryRetryRegistry;

public class Resilience4jBundle<T> implements ConfiguredBundle<T> {

    private final Function<T, Resilience4jConfiguration> resiliencyConfiguratorFunction;

    private final BiConsumer<String, CircuitBreakerConfig.Builder> circuitBreakerConfigurator;

    private final BiConsumer<String, RetryConfig.Builder> retryConfigurator;

    /**
     * Create a new bundle
     */
    public Resilience4jBundle(@NonNull Function<T, Resilience4jConfiguration> resilienceConfiguratorFunction) {
        this(resilienceConfiguratorFunction,
             noOpConfigurator(),
             noOpConfigurator());
    }

    /**
     * Create a new bundle, with a function for modifying CircuitBreaker configurations
     *
     * @param circuitBreakerConfigurator A function that will be passed the name and builder for each circuit breaker before it is created
     */
    public Resilience4jBundle(@NonNull Function<T, Resilience4jConfiguration> resilienceConfiguratorFunction,
                              @NonNull BiConsumer<String, CircuitBreakerConfig.Builder> circuitBreakerConfigurator,
                              @NonNull BiConsumer<String, RetryConfig.Builder> retryConfigurator) {
        this.resiliencyConfiguratorFunction = resilienceConfiguratorFunction;
        this.circuitBreakerConfigurator = circuitBreakerConfigurator;
        this.retryConfigurator = retryConfigurator;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(T dwConfig, Environment environment) {
        Resilience4jConfiguration config = resiliencyConfiguratorFunction.apply(dwConfig);

        List<CircuitBreakerConfiguration> breakerConfigs = config.getCircuitBreakers();
        if (breakerConfigs != null && !breakerConfigs.isEmpty()) {
            InMemoryCircuitBreakerRegistry breakerRegistry = new InMemoryCircuitBreakerRegistry();
            for (CircuitBreakerConfiguration cfg : breakerConfigs) {
                CircuitBreakerConfig.Builder r4jConfigBuilder = cfg.toResilience4jConfigBuilder();

                circuitBreakerConfigurator.accept(cfg.getName(), r4jConfigBuilder);

                breakerRegistry.circuitBreaker(cfg.getName(), r4jConfigBuilder.build());
            }

            config.setCircuitBreakerRegistry(breakerRegistry);

            //Register breaker metrics with Dropwizard metrics
            environment.metrics().registerAll(CircuitBreakerMetrics.ofCircuitBreakerRegistry(breakerRegistry));

            //Register breakers with Jersey for injection, if anybody wants to use it
            environment.jersey().register(new AbstractBinder() {

                @Override
                protected void configure() {
                    //Bind the registry
                    bind(breakerRegistry).to(CircuitBreakerRegistry.class);

                    //Bind each of the breakers
                    for (CircuitBreaker breaker : breakerRegistry.getAllCircuitBreakers()) {
                        bind(breaker).to(CircuitBreaker.class).named(breaker.getName());
                    }
                }
            });
        }

        final List<RetryConfiguration> retryConfigs = config.getRetryConfigurations();
        if (retryConfigs != null & !retryConfigs.isEmpty()) {
            final InMemoryRetryRegistry retryRegistry = new InMemoryRetryRegistry();
            for (final RetryConfiguration cfg : retryConfigs) {
                final RetryConfig.Builder r4jConfigBuilder = cfg.toResilience4jConfigBuilder();
                retryConfigurator.accept(cfg.getName(), r4jConfigBuilder);
                retryRegistry.retry(cfg.getName(), r4jConfigBuilder.build());
            }

            config.setRetryRegistry(retryRegistry);

            //Register retry metrics with Dropwizard metrics
            environment.metrics().registerAll(RetryMetrics.ofRetryRegistry(retryRegistry));

            //Register retryers with Jersey for injection, if anybody wants to use it
            environment.jersey().register(new AbstractBinder() {

                @Override
                protected void configure() {
                    //Bind the registry
                    bind(retryRegistry).to(RetryRegistry.class);

                    //Bind each of the retryers
                    for (final Retry retryer : retryRegistry.getAllRetries()) {
                        bind(retryer).to(Retry.class).named(retryer.getName());
                    }
                }
            });
        }
    }

    private static final BiConsumer<Object, Object> NOOP_CONFIGURATOR = (a, b) -> {
    };

    private static <T, U> BiConsumer<T, U> noOpConfigurator() {
        return (BiConsumer<T, U>) NOOP_CONFIGURATOR;
    }
}
