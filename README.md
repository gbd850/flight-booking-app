# flight-booking-app
## About

Spring Boot flight booking web application based on a monolithic architecture that utilizes newest Java and Spring Framework features and many leading technologies, such as database migrations with Flyway, Redis caching, Docker Compose to run external services as containers and virtual threads from Java 21, to provide a well-rounded, secure and fully functional API. Project uses OAuth2 implementation for Spring Boot to ensure security across features of the application.



## Run the application
```bash
$ git clone https://github.com/gbd850/flight-booking-app.git

$ cd flight-booking-app

$ mvnw spring-boot:run

$ cd authserver

$ mvnw spring-boot:run

$ cd ../frontend

$ ng serve
```
The main application runs on `localhost:8080` while the authentication server runs on port `8081`

## Specification

### Tech Stack

#### Backend

* Java 21
* Spring Boot 3
* Spring Data
* Spring Security
* Spring Actuator
* OAuth2
* Docker
* MySQL
* Redis
* Flyway
* JUnit 5
* Testcontainers
* Mockito
* AssertJ
* Rest Assured
* Hamcrest
* Wavefront
* Resilience4j

#### Frontend

* TypeScript
* Angular 17
* RxJS
* TailwindCSS
