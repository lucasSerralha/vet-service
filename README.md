# Vet Service

## Project Title & Description
**Service Name:** Vet Service  
**Bounded Context:** Veterinarian / Staff Domain  

The Vet Service is a core microservice within the Spring PetClinic ecosystem responsible for managing the veterinarian domain. Its primary focus is the lifecycle, details, and specializations of the clinic's veterinarians. This service encapsulates all business logic and data regarding vet profiles and their respective clinical specialties, ensuring high cohesion and decoupling from other domains.

## Tech Stack
* **Java 21:** Leveraging modern language features for performance and readability.
* **Spring Boot 3.2.x:** Core framework for rapid microservice development and autoconfiguration.
* **Spring Data JPA & H2:** For robust data persistence and lightweight in-memory database during local development.
* **Spring Cloud Netflix Eureka:** For service discovery and registration within the microservice landscape.
* **RabbitMQ:** Message broker for asynchronous, event-driven communication.
* **Resilience4j:** Fault-tolerance library implementing Circuit Breaker and Time Limiter patterns.
* **OpenTelemetry & Micrometer:** For distributed tracing and observability.
* **SpringDoc OpenAPI (Swagger):** For automated API contract documentation.

## Architectural Decisions

### Synchronous vs. Asynchronous Communication
**Decision:** We utilize synchronous REST for data retrieval/validation, and asynchronous RabbitMQ events for state propagation.  
**Rationale:** Synchronous REST over HTTP is used for immediate, read-heavy operations (e.g., fetching a vet's details or validating a specialty) where the client requires an immediate response to proceed. For state changes that affect other domains, we employ Eventual Consistency via RabbitMQ. This asynchronous approach prevents tight coupling, meaning the Vet Service can publish events without waiting for downstream services, improving overall system latency and reliability.

### Resilience
**Decision:** Implementation of Resilience4j (Circuit Breaker and Time Limiter).  
**Rationale:** To prevent cascading failures and resource exhaustion across the microservice ecosystem, Resilience4j is implemented. If a downstream dependency or database becomes unresponsive, the Time Limiter ensures threads are not blocked indefinitely. Concurrently, the Circuit Breaker monitors failure rates and "opens" to fail-fast, allowing the failing service time to recover while providing fallback mechanisms or graceful degradation to the client.

### Error Handling
**Decision:** Strict adherence to RFC 7807 (Problem Details for HTTP APIs).  
**Rationale:** We standardize our error responses using the RFC 7807 format to provide clients with consistent, machine-readable problem details. We specifically handle distinct HTTP status codes—such as `404 Not Found` for missing resources (e.g., vet ID does not exist) and `409 Conflict` for business rule violations (e.g., attempting to delete a specialty currently in use). This clear semantic differentiation distinguishes pure business logic errors from infrastructure or internal server errors (`500`), improving API consumability.

## API Endpoints

| HTTP Method | Endpoint | Description | Responses |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/vets` | Listar todos os veterinários (Paginated) | `200 OK` |
| **GET** | `/api/vets/{vetId}` | Obter detalhes de um veterinário | `200 OK`, `404 Not Found` |

## How to Run

### Prerequisites
* Java 21 installed.
* Maven installed (or use the provided Maven wrapper).
* Docker (Optional, if running dependent infrastructure locally like RabbitMQ or Eureka Server).

### Running Locally
1. **Navigate to the project directory:**
   ```bash
   cd /home/lucas/ASID/vet-service
   ```
2. **Build the application:**
   ```bash
   mvn clean install
   ```
3. **Start the service:**
   ```bash
   mvn spring-boot:run
   ```
   *The service will start locally, typically on port 8081, and will attempt to register with the Eureka server if configured.*

4. **Access API Documentation:**
   Once running, you can access the Swagger UI interface to view and test the endpoints at:  
   `http://localhost:8081/swagger-ui.html` (or the configured port).
