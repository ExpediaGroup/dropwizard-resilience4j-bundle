# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/).

## [3.0.0] - TBD
### Changed
- Upgrade to Dropwizard 2.x.
- Switched to Java 11
- Switched from JUnit 4 to JUnit 5
- Renamed artifact groupId from `com.expediagroup` to `com.expediagroup.dropwizard`
- Update to R4j `1.5.0`
- Replace the use of deprecated `io.github.resilience4j.retry.IntervalFunction` with `io.github.resilience4j.core.IntervalFunction`

## [2.0.1] - 2020-03-20
### Changed
- Updated to R4j 1.3.1. This is a minor version bump that has several changes that will be user-visible. See R4j documentation for details.

| Package | Update | Change |
|---|---|---|
| org.apache.maven.plugins:maven-compiler-plugin | patch | `3.8.0` -> `3.8.1` |
| org.apache.maven.plugins:maven-javadoc-plugin | minor | `3.1.1` -> `3.2.0` |
| org.apache.maven.plugins:maven-source-plugin | minor | `3.0.1` -> `3.2.1` |
| org.apache.maven.plugins:maven-surefire-plugin | patch | `2.22.1` -> `2.22.2` |
| org.glassfish.hk2:hk2-api | minor | `2.5.0-b32` -> `2.6.1` |
| io.vavr:vavr | patch | `0.10.0` -> `0.10.2` |
| javax.ws.rs:javax.ws.rs-api | minor | `2.0.1` -> `2.1.1` |
| junit:junit | minor | `4.12` -> `4.13` |
| io.dropwizard:dropwizard-bom | patch | `1.3.8` -> `1.3.20` |

## [2.0.0] - 2020-01-06
### Changed
- Renamed artifact groupId from `com.expediagroup.dropwizard.bundle` to `com.expediagroup`
- Relocated classes from `com.homeaway.dropwizard.bundle.resilience4j` to `com.expediagroup.dropwizard.resilience4j`

## [1.0.1] - 2019-10-31
### Changed
- Fixed crash at startup when no `retryConfigurations` are specified in the config

## [1.0.0] - 2019-10-27
### Added
- Added support for R4j `Retry`

### Changed
- Updated to R4j 1.1.0. This is a major version bump that has a lot of breaking changes that will be user-visible. See R4j documentation for details.

## [0.0.1] - 2019-03-11
### Initial Version
- Created with initial support for CircuitBreakers
