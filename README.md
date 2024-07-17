# hotelmanager-spring
This repository represents the implementation of the eXXellent-nights hotelmanager backend. As the name suggests, it is written with the Spring Boot framework and is fully equipped to manage hotel rooms and bookings efficiently.

## Quickstart

You just need to run the fully prepared `docker-compose.yml` file:
````shell
docker compose up
````

### What to expect?

In the first step, the PostgreSQL database will be started. When the health check passes, a new image of the current (Spring Backend) codebase will be built, and a container with this image will be run. This container will also undergo a health check - if it passes, the Angular frontend will be executed.

Now, navigate to http://localhost:4200/ to browse, add, delete, and update all hotel rooms and bookings.

Want to test with Postman? The Spring Backend is exposed on port 8080.

## Features

- Manage hotel rooms (create, read, update, delete)
- Manage bookings (create, read, update, delete)
- Check room availability
- Filter hotel rooms by filter specification
- Validate booking dates (start and end date)
- Tested with Testcontainers instead of H2
- Swagger UI integrated at /swagger-ui/index.html
- Actuator integrated for readiness/livenessprobe endpoint support
- Customized Spring Boot Banner
- Initial loading data on startup
- docker-compose.yml for local development provided
- docker compose also contains the frontend component. Frontend repository is this https://github.com/teceP/hotelmanager-angular
- Dockerfile for local development provided (also executed in docker-compose file)
- Spring build-image goal integrated, run with: mvn spring-boot:build-image
- Well documented all methods and classes