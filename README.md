dropwizard-resilience4j-bundle [![Build Status][build-icon]][build-link]
==============================
                        
[build-icon]: https://travis-ci.org/homeaway/dropwizard-resilience4j-bundle.svg?branch=master
[build-link]: https://travis-ci.org/homeaway/dropwizard-resilience4j-bundle


Lightweight integration of Resilience4J into Dropwizard configuration and metrics. Does not provide any other services - actually _using_
all the Resilience4j stuff is up to you.

Currently this only supports Circuit Breakers, but supporting [more R4j features is welcome](http://resilience4j.github.io/resilience4j/#_usage_guide).


User Guide
==============================

In your POM...
```xml
<dependency>
    <groupId>com.expediagroup.dropwizard.bundle</groupId>
    <artifactId>dropwizard-resilience4j-bundle</artifactId>
    <version>0.1.0</version> <!-- use latest -->
</dependency>
```

In your config.yaml...
```yaml
resilience4j:
    circuitBreakers:
        - name: myFancyCircuitBreaker
          waitDurationInOpenState: 30s
          ringBufferSizeInClosedState: 30
        ## Add as many as you want
        ## All parameters are optional except `name`, defaults are documented in CircuitBreakerConfiguration.java
        ##- name: anotherCircuitBreaker
```

Add to your application's Config class:
```yaml
@NotNull
private Resilience4jConfiguration resilience4j;
```

The circuit breakers are automatically wired into Dropwizard Metrics, and also [into HK2 using the name from the YAML](src/main/java/com/homeaway/dropwizard/bundle/resilience4j/Resilience4jBundle.java#L93).
You can also retrieve them from the configuration class...

```java
val breaker = myAppConfig.getResilience4j().getCircuitBreakerRegistry().circuitBreaker("myFancyCircuitBreaker");
```

If you want to configure the CircuitBreakers in code before they are created, you can pass a handler into the bundle when it's constructed.
```java
@Override
public void initialize(Bootstrap<RatesEngineConfiguration> bootstrap) {
    bootstrap.addBundle(new Resilience4jBundle<>(RatesEngineConfiguration::getResilience4j, (breakerName, breakerBuilder) -> {
                //breakerName is what was configured in YAML
                //breakerBuilder can be modified as desired
                breakerBuilder.ignoreExceptions(IOException.class);
            }));
}
```
