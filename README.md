# Sushil POC Trade Store

## Overview
This project is a production-grade Spring Boot application for a trade store, designed to demonstrate best practices for banking/fintech backend systems. It features dual persistence (SQL & NoSQL), streaming/queueing, robust error handling, TDD, and a CI/CD pipeline. The codebase is modular, extensible, and ready for real-world banking requirements.

---

## Configuration & Secrets

- All sensitive values (DB passwords, API keys, etc.) should be provided via environment variables or a local `application.yml` that is not committed to the repository.
- A template config file is provided as `src/main/resources/application-template.yml`. Copy this to `application.yml` and fill in your real values locally.
- For CI/CD, use GitHub Actions secrets or your CI tool’s secret manager.

- File has been shared to HR as part of initial communication.


---

## Request Flow


```
Client (Postman/Frontend/Service)
	-->  Controller (DTO & Validation)
	-->  Service Layer (Business Logic)
	-->  In-Memory Queue (Async Processing)
	-->  Consumer Thread
	-->  Persistence Layer [MySQL | MongoDB]
	-->  Metrics & Logging
	-->  Structured API Response
```

---

## Request Flow Explained

1. **API Request Received**
	 - A client (e.g., Postman, frontend, or another service) sends a REST API request to the application (e.g., to create a trade).
	 - *Why*: Exposes business functionality in a secure, versioned, and documented way.

2. **Controller Layer (DTO & Validation)**
	 - The request is handled by a controller, which maps input to a DTO and applies validation rules.
	 - *Why*: Ensures only well-formed, valid data enters the system, preventing errors and security issues early.

3. **Service Layer (Business Logic)**
	 - The controller delegates to a service, which contains the core business logic (e.g., trade validation, expiry checks, versioning).
	 - *Why*: Centralizes business rules for maintainability, testability, and separation of concerns.

4. **Queueing (Async Processing)**
	 - For certain operations (e.g., trade ingestion), the service enqueues the request to the in-memory queue for asynchronous processing.
	 - *Why*: Decouples API responsiveness from backend processing, improves scalability, and simulates real-world streaming.

5. **Persistence (SQL & NoSQL)**
	 - Trades are persisted to both MySQL (for transactional integrity) and MongoDB (for analytics/flexibility).
	 - *Why*: Demonstrates dual persistence patterns common in banking for compliance and analytics.

6. **Metrics & Logging**
	 - Throughout the flow, metrics are collected (e.g., queue size, processing time) and key events/errors are logged.
	 - *Why*: Enables observability, monitoring, and rapid troubleshooting—critical for production systems.

7. **Response Returned**
	 - The API returns a structured response (or error) to the client, following REST and error-handling best practices.
	 - *Why*: Ensures a consistent, predictable client experience and simplifies integration.

This flow ensures the system is robust, maintainable, and ready for real-world banking requirements, while remaining easy to extend and operate.

---

## Component Rationale

- **Spring Boot**: Provides rapid development, convention-over-configuration, and a rich ecosystem for building robust, testable, and maintainable Java applications. Widely adopted in banking for its reliability and enterprise support.
- **MySQL (SQL Database)**: Used for transactional integrity, strong consistency, and compliance needs. Ideal for storing trades where ACID guarantees and reporting are critical.
- **MongoDB (NoSQL Database)**: Enables flexible schema, high-volume ingestion, and analytics. Useful for storing trade events, audit logs, or data that may evolve over time.
- **In-Memory Queue**: Simulates asynchronous trade processing and decouples API from backend processing. Demonstrates streaming/queueing patterns; can be replaced with Kafka for production.
- **Prometheus & Micrometer**: Collects and exposes application and queue metrics for monitoring and alerting. Essential for observability and operational excellence in modern systems.
- **OpenAPI/Swagger**: Automatically documents REST APIs, enabling easy testing, integration, and onboarding for consumers and partners.
- **JUnit (TDD)**: Ensures code quality and reliability through automated unit and integration tests. Supports test-driven development and regression safety.
- **Maven**: Manages dependencies, builds, and plugins, ensuring reproducible builds and easy integration with CI/CD pipelines.
- **CI/CD Pipeline (GitHub Actions):** Automates build, test, and security scanning using GitHub Actions (see `.github/workflows/ci-cd.yml`).
	- Supports multi-branch, release tagging, and enterprise branching strategy. See [workflow README](.github/workflows/README.md) for details and extension instructions. Jenkins pipeline can be provided for enterprise/on-premise use if required.
- **PlantUML**: Used for architecture, class, and sequence diagrams to communicate design and support documentation.
- **Postman Collection**: Provides ready-to-use API requests for manual or automated testing and demonstration.

Each component is chosen to balance real-world banking requirements (compliance, reliability, scalability) with ease of demonstration and extensibility for future enhancements.

---

## Phase 1: Core Implementation

### Features
- **Domain Model & Business Logic:** Clean, extensible, and well-tested trade management.
- **REST API:** Versioned endpoints (`/api/v1/`), DTOs, OpenAPI/Swagger documentation, and Postman collection.
- **Input Validation & Sanitization:** Spring validation for all API inputs.
- **Error Handling:** Centralized exception handling with clear error responses.
- **In-Memory Queue:** Async trade processing with clear abstraction for future upgrades.
- **Dual Persistence:** MySQL (SQL) and MongoDB (NoSQL) for demo and analytics.
- **Health & Metrics Endpoints:** Spring Actuator, Prometheus metrics.
- **CI/CD Pipeline:** Automated build, test, and security scan using GitHub Actions (see `.github/workflows/ci-cd.yml`).
	- Supports multi-branch, release tagging, and enterprise branching strategy. See [workflow README](.github/workflows/README.md) for details and extension instructions. Jenkins pipeline can be provided for enterprise/on-premise use if required.
- **Testing:** JUnit-based unit and integration tests.
- **Documentation:** PlantUML diagrams, OpenAPI, and this README.

### Reference Code
- Main entry: `src/main/java/com/sushil/poc/SushilPocApplication.java`
- REST controllers: `src/main/java/com/sushil/poc/controller/`
- DTOs: `src/main/java/com/sushil/poc/dto/`
- Queue: `src/main/java/com/sushil/poc/queue/InMemoryQueue.java`
- Persistence: `src/main/java/com/sushil/poc/repository/`
- Service logic: `src/main/java/com/sushil/poc/service/`
- Tests: `src/test/java/com/sushil/poc/TradeServiceTests.java`
- CI/CD: `.github/workflows/` 
- API docs: `/swagger-ui.html` (after running the app)
- Postman: `src/main/java/com/sushil/poc/Postman/Sushil_POC.postman_collection.updated.json`

---

## Phase 2: Security, Scalability, and Compliance (Planned)

### What, Why, and How

- **OAuth2/JWT authentication, role-based access control**
	- *What*: Secure all endpoints with OAuth2/JWT and restrict access by user roles (e.g., trader, admin).
	- *Why*: Protect sensitive trade data and ensure only authorized users can perform actions, meeting banking security standards.
	- *How*: Integrate Spring Security with OAuth2/JWT, define roles/authorities, and annotate endpoints with access rules.

- **Rate limiting (API abuse prevention)**
	- *What*: Limit the number of requests per user/IP to prevent abuse or accidental overload.
	- *Why*: Protects backend resources, ensures fair usage, and mitigates denial-of-service risks.
	- *How*: Use Bucket4j, Redis, or API Gateway rate limiting features; configure limits and error responses.

- **Audit logging for all trade actions**
	- *What*: Record all create, update, and delete actions on trades, including user, timestamp, and before/after state.
	- *Why*: Enables traceability, compliance, and forensic analysis—critical for banking audits.
	- *How*: Implement an audit log service or use AOP to intercept and log trade-modifying actions to a secure store.

- **Idempotency for trade submissions**
	- *What*: Ensure repeated trade submissions (e.g., due to retries) do not create duplicates.
	- *Why*: Prevents financial errors and supports safe, reliable integrations with external systems.
	- *How*: Use idempotency keys in API requests and store processed keys to detect/reject duplicates.

- **Dockerization and docker-compose for local/CI**
	- *What*: Package the app and dependencies as Docker containers for consistent local and CI environments.
	- *Why*: Simplifies onboarding, testing, and deployment; matches modern DevOps practices.
	- *How*: Write a Dockerfile for the app, a docker-compose.yml for services (DB, Mongo, app), and document usage.

- **Enhanced error response standardization**
	- *What*: Return structured, consistent error objects with codes, messages, and details for all API errors.
	- *Why*: Improves client experience, debuggability, and integration with frontend or partner systems.
	- *How*: Define a global error response schema, update exception handlers, and document error formats in OpenAPI.

---

## Phase 3: Cloud-Native, Observability, and Enterprise Readiness (Planned)

### What, Why, and How

- **Kafka for streaming (replace in-memory queue)**
	- *What*: Use Apache Kafka as the backbone for trade event streaming and processing.
	- *Why*: Kafka provides durability, scalability, and high-throughput messaging, which are essential for real-time banking systems.
	- *How*: Integrate Kafka client libraries, refactor the queue abstraction to use Kafka topics, and update producers/consumers accordingly.

- **Distributed tracing (OpenTelemetry)**
	- *What*: Add distributed tracing to track requests and events across microservices and infrastructure.
	- *Why*: Enables deep visibility into system performance, bottlenecks, and root cause analysis in complex, distributed environments.
	- *How*: Integrate OpenTelemetry SDK, instrument key flows, and export traces to Zipkin, Jaeger, or a cloud tracing backend.

- **Kubernetes manifests, Helm charts**
	- *What*: Provide deployment descriptors for Kubernetes and Helm for automated, repeatable deployments.
	- *Why*: Facilitates cloud-native, scalable, and resilient deployments, supporting modern DevOps practices.
	- *How*: Write Kubernetes YAML manifests and Helm charts for all services, document deployment steps, and support configuration via values files.

- **Infrastructure as Code (Terraform)**
	- *What*: Use Terraform to provision and manage cloud infrastructure (databases, Kafka, K8s clusters, etc.).
	- *Why*: Ensures reproducibility, auditability, and automation of infrastructure changes, reducing manual errors.
	- *How*: Write Terraform modules for all required resources, version control the code, and document usage for different environments.

- **Advanced monitoring, alerting, and disaster recovery**
	- *What*: Implement comprehensive monitoring, alerting, and backup/restore strategies.
	- *Why*: Ensures operational excellence, rapid incident response, and business continuity in case of failures.
	- *How*: Integrate Prometheus/Grafana for metrics, set up alert rules, automate backups, and document disaster recovery procedures.

---

## Roadmap
- [x] Phase 1: Core, CI/CD, and documentation
- [ ] Phase 2: Security, compliance, and containerization
- [ ] Phase 3: Cloud-native, observability, and enterprise features

---

