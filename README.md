dropwizard-resilience4j-bundle [![Build Status][build-icon]][build-link]
==============================
                        
[build-icon]: https://travis-ci.org/homeaway/dropwizard-resilience4j-bundle.svg?branch=master
[build-link]: https://travis-ci.org/homeaway/dropwizard-resilience4j-bundle


Lightweight integration of Resilience4J into Dropwizard configuration and metrics. Does not provide any other services - actually _using_
all the Resilience4j stuff is up to you. The [R4J documentation](https://resilience4j.readme.io/docs) is pretty good.

Currently, this only supports Circuit Breakers and Retries, but supporting [more R4j features is welcome](http://resilience4j.github.io/resilience4j/#_usage_guide).

User Guide
==============================

In your POM...
```xml
<dependency>
    <groupId>com.expediagroup.dropwizard</groupId>
    <artifactId>dropwizard-resilience4j-bundle</artifactId>
    <version>3.0.0</version> <!-- use latest -->
</dependency>
```

In your config.yaml...
```yaml
resilience4j:
    circuitBreakers:
        - name: myFancyCircuitBreaker
          waitDurationInOpenState: 30s
          failureRateThreshold: 10
        ## Add as many as you want
        ## All parameters are optional except `name`, defaults are documented in CircuitBreakerConfiguration.java
        ##- name: anotherCircuitBreaker
    retryConfigurations:
        - name: exponentialRandomizedBackoffRetry
          maxAttempts: 4
          intervalFunction:
            type: exponentialRandomBackoff
            initialInterval: 10ms
            multiplier: 2.5
            randomizationFactor: 0.5
        ## Add as many as you want
        ## most parameters are optional, but `intervalFunction` is required. Several are available, see `IntervalFunctionFactory` for full list, 
        ## but currently: constant, randomized, exponentialBackoff, exponentialRandomBackoff
```

Add to your application's Config class:
```java
@NotNull
private Resilience4jConfiguration resilience4j;
```

Configured R4J objects are automatically wired into Dropwizard Metrics, and also [into HK2 using the name from the YAML](src/main/java/com/expediagroup/dropwizard/resilience4j/Resilience4jBundle.java#L93).
You can also retrieve them from the configuration class...

```java
val breaker = myAppConfig.getResilience4j().getCircuitBreakerRegistry().circuitBreaker("myFancyCircuitBreaker");
val retry = myAppConfig.getResilience4j().getRetryRegistry().retry("myFancyRetry");
```

If you want to configure the objects in code before they are created, you can pass a handler into the bundle when it's constructed.
```java
@Override
public void initialize(Bootstrap<RatesEngineConfiguration> bootstrap) {
    bootstrap.addBundle(new Resilience4jBundle<>(TestConfiguration::getResilience4j,
                                                 (breakerName, breakerBuilder) -> {
                                                     //breakerName is what was configured in YAML
                                                     //breakerBuilder can be modified as desired
                                                     breakerBuilder.ignoreExceptions(IOException.class);
                                                 },
                                                 (retryName, retryBuilder) -> {
                                                     //retryName is what was configured in YAML
                                                     //retryBuilder can be modified as desired
                                                     retryBuilder.retryOnResult(myRetryPredicate);
                                                 }));
}
```
