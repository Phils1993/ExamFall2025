# Eksempel på README file:


# Trip Management Backend

## Overview
This project is a **Java backend application** built with **Javalin** for REST APIs and **JPA/Hibernate** for persistence.  
It models a travel domain with entities such as **Trip** and **Guide**, and demonstrates clean layering with **DAO**, **Service**, **DTO**, and **Mapper** classes.

The project highlights **exam‑relevant design decisions** such as:
- Use of **DTOs** to decouple persistence from API payloads.
- Use of **guideId** in `TripDTO` for safe updates.
- Use of **JOIN FETCH** queries to avoid `LazyInitializationException`.
- Difference between **patch‑style updates** and full entity replacement.

---

## Architecture
- **Entities**: Annotated with JPA (`Trip`, `Guide`, plus `User`/`Role` if included).
- **DTOs**: Data Transfer Objects (`TripDTO`, `GuideDTO`).
- **Mappers**: Convert between entities and DTOs (`TripMapper`, `GuideMapper`).
- **DAO Layer**: Encapsulates persistence logic using `EntityManager`.
- **Service Layer**: Business logic, update handling, orchestration.
- **Controller Layer**: Javalin endpoints exposing CRUD operations.

---

## Entity Relationships
- **Trip ↔ Guide**
    - `Trip` has a `@ManyToOne(fetch = LAZY)` relationship to `Guide`.
    - `Guide` has a `@OneToMany(mappedBy = "guide")` back‑reference to trips.

- **Users ↔ Roles** (if included)
    - Many‑to‑many via a join table (`user_roles`).

---

## Update Logic
- **Trip Updates**
    - Patch‑style: only update fields present in the DTO.
    - If `guideId` is provided, the guide is reassigned.
    - If not, the existing guide remains unchanged.
    - After update, the entity is **re‑fetched with `JOIN FETCH`** to ensure the `Guide` is initialized and avoid `LazyInitializationException`.

---

## Example Endpoints

### Trips
- **GET /api/v1/trips** → list all trips
- **GET /api/v1/trips/{id}** → get trip by id (with guide and packing list)
- **POST /api/v1/trips** → create a new trip
- **PUT /api/v1/trips/{id}** → update trip (patch‑style, preserves guide unless `guideId` provided)
- **DELETE /api/v1/trips/{id}** → delete trip
- **PUT /api/v1/trips/{tripId}/guides/{guideId}** → assign a guide to a trip
- **GET /api/v1/trips/guides/totalprice** → aggregated trip values per guide
- **GET /api/v1/trips/{id}/packing/weight** → calculate total packing weight for a trip’s category

### Guides
- **GET /api/v1/guides** → list all guides
- **GET /api/v1/guides/{id}** → get guide by id
- **POST /api/v1/guides** → create a new guide
- **PUT /api/v1/guides/{id}** → update guide
- **DELETE /api/v1/guides/{id}** → delete guide

---

## Testing Strategy

### DAO Tests
- **Database**: In‑memory H2 database configured via `persistence.xml`.
- **Coverage**:
    - `create`, `findById`, `getAll`, `update`, `delete` for both `TripDAO` and `GuideDAO`.
    - Custom queries: `getCategory`, `assignGuide`, `getTotalTripValuePerGuide`.
- **Approach**: Each test runs in isolation with schema auto‑created/dropped. Assertions verify persistence and query correctness.

### Controller Tests
- **Frameworks**: JUnit 5 + RestAssured.
- **Setup**:
    - `ApplicationConfig.startServerForTest` boots Javalin on port 7070.
    - `TestPopulator` seeds a known `Trip` and `Guide` into the test database.
- **Coverage**:
    - CRUD endpoints for `/api/v1/trips` and `/api/v1/guides`.
    - Custom endpoints: `assignGuide`, `getTotalTripValuePerGuide`, `getPackingWeight`.
- **Assertions**: Verify HTTP status codes (200, 201, 204, 404) and JSON response bodies.

### Example Test (Controller)
```java
@Test
void testGetTripById() {
    given()
        .when().get("/api/v1/trips/" + testTripId)
        .then().statusCode(200)
        .body("id", equalTo((int) testTripId));
}
````

# Andet eksempel på README file

# Item Booking Application – Backend Exam Fall 2024

**Item Booking Application** is a REST API developed as an exam project focused on architecture, security, testability, and proper handling of entity relationships.  
The application is backend-only and can be run locally via Docker Compose.

---

## Technologies
- **Javalin** – Lightweight Java web framework
- **Hibernate (JPA)** – ORM for database handling with PostgreSQL
- **Lombok** – Reduces boilerplate using annotations like `@Getter`, `@Builder`, `@AllArgsConstructor`
- **JWT (nimbus-jose-jwt + jbcrypt)** – Token-based authentication and authorization
- **RestAssured + JUnit + Hamcrest** – Integration testing with precise assertions
- **Docker Compose** – Container-based setup for database and application
- **SLF4J + Logback** – Logging of requests, responses, and errors

---

## Focus Areas
- Role-based authorization via Javalin middleware
- Token-based authentication using JWT
- DTO mapping to prevent infinite recursion between `Item` and `Student`
- Global exception handling with structured JSON error responses
- Health checks and container logging
- Testability through integration tests and deterministic test data

---

## Architecture Overview
The project follows a classic layered structure:

| Layer | Description |
|-------|--------------|
| **Controller** | Receives and validates requests, returns DTOs |
| **DAO (Hibernate)** | Handles database operations |
| **DTOs** | Ensures correct serialization without recursion |
| **Security** | JWT validation and role-based control in middleware |

---

## Endpoints and Access
**Base URL:**  http://localhost:7070/api/v1

### Items
| Method | Endpoint | Description |
|--------|-----------|-------------|
| GET    | /items | Get all items |
| GET    | /items/{id} | Get item by ID |
| POST   | /items | Create a new item |
| PUT    | /items/{id} | Update an item |
| DELETE | /items/{id} | Delete an item |
| PUT    | /items/{itemId}/students/{studentId} | Assign an item to a student |
| POST   | /items/populate | Populate the database with test data |
| GET    | /items/category/{cat} | Get items filtered by category (Task 5.1) |
| GET    | /items/summary/total-purchase | Total purchasePrice per student (Task 5.2) |

### Categories
Valid values for `{cat}`:  
`VIDEO`, `VR`, `SOUND`, `PRINT`, `TOOL`

---

## Example JSON Response
**Task 5.2 – Total purchase price per student**
```json
[
  { "studentId": 1, "totalPurchasePrice": 3700 },
  { "studentId": 2, "totalPurchasePrice": 1100 },
  { "studentId": 3, "totalPurchasePrice": 1500 },
  { "studentId": 4, "totalPurchasePrice": 0 }
]

````
# Theoretical Question

Why is PUT used to assign an item to a student instead of POST?
Because the operation is idempotent – it updates an existing resource (item.student).
Repeated calls yield the same result.
POST is used for creating new resources, while PUT is used for updating or replacing existing ones.

# GitHub

Repository: ItemBookingAPIExamOkt-025

User: phil1993
