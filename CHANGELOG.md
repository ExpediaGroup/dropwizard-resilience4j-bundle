# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/).

## [Unreleased]
### Changed
- Updated to R4j 1.3.1. This is a minor version bump that has several changes that will be user-visible. See R4j documentation for details.

## [2.0.0] - 2019-01-06
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
