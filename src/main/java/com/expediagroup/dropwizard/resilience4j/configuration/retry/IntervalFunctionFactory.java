package com.expediagroup.dropwizard.resilience4j.configuration.retry;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.dropwizard.jackson.Discoverable;
import io.github.resilience4j.core.IntervalFunction;

/**
 * A service provider interface for creating Resilience4j {@link IntervalFunction interval functions}.
 * To create your own, just:
 * <ol>
 *     <li>Create a class which implements {@link IntervalFunctionFactory}.</li>
 *     <li>Annotate it with {@code @JsonTypeName} and give it a unique type name.</li>
 *     <li>Add it to the {@code @JsonSubTypes} annotation on this class</li>
 * </ol>
 *
 * @see ConstantIntervalFunctionFactory
 * @see ExponentialBackoffFunctionFactory
 * @see ExponentialRandomBackoffFunctionFactory
 * @see RandomizedIntervalFunctionFactory
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ConstantIntervalFunctionFactory.class),
    @JsonSubTypes.Type(value = ExponentialBackoffFunctionFactory.class),
    @JsonSubTypes.Type(value = ExponentialRandomBackoffFunctionFactory.class),
    @JsonSubTypes.Type(value = RandomizedIntervalFunctionFactory.class)
})
public interface IntervalFunctionFactory extends Discoverable {

    IntervalFunction build();
}
