# Schedulify

Organizing conference schedules efficiently.

## Overview

Schedulify is an API designed to schedule presentations for a conference, creating different tracks for the presentations. The API receives a list of presentations with their subjects and durations, and organizes them into tracks based on specific rules.

## Features

- Schedule presentations for both morning and afternoon sessions.
- Automatically includes a lunch break at 12:00 PM.
- No breaks between presentations.
- Schedule Networking Event if presentations ends before 17:00 PM.

## API Documentation

The API documentation is generated using Swagger and can be accessed at:
[Swagger UI](http://localhost:8080/swagger-ui/index.html)

## Project Dependencies
 - Java 17
 - Spring Boot 3.3.0
 - Spring Boot Starter Validation
 - Spring Boot Starter Web
 - Spring Boot DevTools
 - Lombok
 - Spring Boot Starter Test
 - SpringDoc OpenAPI Starter WebMVC UI
 - Mockito Core

### Prerequisites
 - Java 17 must be installed on your computer.

## Getting Started

### Clone the repository
```sh
git clone https://github.com/cmaktas/schedulify.git
```
### Navigate to the project directory
```sh
cd schedulify
```
## Run the application
### Using Maven
```sh
mvn clean install
mvn spring-boot:run
```

### Using Docker
```sh
./run.sh
```

### Contact
Cem Aktas - cemaktas@ymail.com_