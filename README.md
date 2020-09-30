# marklogic-data-hub-job-explorer

[Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/index.html) application to explore the [MarkLogic Data Hub](https://docs.marklogic.com/datahub) JOBS database and run flows.

## Quick Start

1. Create a new properties file for your environment, e.g. `src/main/resources/application-local.properties`.
2. Create the war file using `gradlew bootWar`.
3. Run the application with `java -jar -Dspring.profiles.active=local build\libs\marklogic-data-hub-job-explorer-1.0.0.war` (make sure you use the environment name from step 1.)
4. Access the UI at `http://localhost:8090/jobs`
