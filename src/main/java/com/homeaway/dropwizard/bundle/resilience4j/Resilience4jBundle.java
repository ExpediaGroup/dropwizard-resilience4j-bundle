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
import javax.annotation.Nonnull;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import com.homeaway.dropwizard.bundle.resilience4j.configuration.CircuitBreakerConfiguration;
import com.homeaway.dropwizard.bundle.resilience4j.configuration.Resilience4jConfiguration;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.internal.InMemoryCircuitBreakerRegistry;
import io.github.resilience4j.metrics.CircuitBreakerMetrics;

public class Resilience4jBundle<T> implements ConfiguredBundle<T> {

    private final Function<T, Resilience4jConfiguration> resiliencyConfiguratorFunction;

    private final BiConsumer<String, CircuitBreakerConfig.Builder> circuitBreakerConfigurator;

    private static final BiConsumer<String, CircuitBreakerConfig.Builder> NOOP_CONFIGURATOR = (a, b) -> {
    };

    /**
     * Create a new bundle
     */
    public Resilience4jBundle(@Nonnull Function<T, Resilience4jConfiguration> resilienceConfiguratorFunction) {
        this(resilienceConfiguratorFunction, NOOP_CONFIGURATOR);
    }

    /**
     * Create a new bundle, with a function for modifying CircuitBreaker configurations
     *
     * @param circuitBreakerConfigurator A function that will be passed the name and builder for each circuit breaker before it is created
     */
    public Resilience4jBundle(@Nonnull Function<T, Resilience4jConfiguration> resilienceConfiguratorFunction,
                              @Nonnull BiConsumer<String, CircuitBreakerConfig.Builder> circuitBreakerConfigurator) {
        this.resiliencyConfiguratorFunction = resilienceConfiguratorFunction;
        this.circuitBreakerConfigurator = circuitBreakerConfigurator;
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
    }
}
