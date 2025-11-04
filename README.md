# Candidate Management Backend

A Java backend built with **Javalin (REST)** and **JPA/Hibernate (persistence)** that models candidates, skills, and candidate-skill links.  
The codebase follows a layered architecture (**Entity → DAO → Service → Controller → DTO/Mapper**) and includes deterministic integration tests (**JUnit 5 + RestAssured**) with a **TestPopulator** for reproducible test data.

---

## Overview

This project demonstrates exam-relevant design decisions and production-grade patterns:

- Clear layering (DAO, Service, Controller, DTO, Mapper)
- Use of DTOs to decouple API payloads from persistence models
- Id-based relational operations to avoid detached entity issues
- `JOIN FETCH` queries for DTO mapping to avoid `LazyInitializationException`
- Patch-style updates that update only fields present in DTOs

---

## Architecture

**Entities:** `Candidate`, `Skill`, `CandidateSkill`, …  
**DTOs:** `CandidateDTO`, `CandidateCreateDTO`, etc.  
**Mappers:** Convert Entities ↔ DTOs (centralized mapping logic)  
**DAO Layer:** Encapsulates persistence, manages `EntityManager` + transactions  
**Service Layer:** Business rules, validation, orchestration across DAOs  
**Controller Layer:** Javalin handlers exposing REST endpoints under `/api/v1`

---

## Entity Relationships

`Candidate` ↔ `CandidateSkill` ↔ `Skill` (many-to-many via `CandidateSkill`)

Use id-based operations for relational changes, for example:
```java
candidateDAO.addSkill(candidateId, skillId);
````

# API (base: /api/v1)
````
| Method | Endpoint                                     | Description                                                                         |
| ------ | -------------------------------------------- | ----------------------------------------------------------------------------------- |
| GET    | `/candidates`                                | List candidates; optional `category` param (e.g. `PROG_LANG`)                       |
| GET    | `/candidates/{id}`                           | Get candidate by ID (includes skills and enrichment fields)                         |
| POST   | `/candidates`                                | Create candidate (`CandidateCreateDTO`: name, phone, education, skillIds) → **201** |
| PUT    | `/candidates/{id}`                           | Update candidate (patch-style) → **200**                                            |
| DELETE | `/candidates/{id}`                           | Delete candidate → **204**                                                          |
| PUT    | `/candidates/{candidateId}/skills/{skillId}` | Link skill → **200** (returns updated `CandidateDTO`)                               |
| DELETE | `/candidates/{candidateId}/skills/{skillId}` | Unlink skill → **200** (returns updated `CandidateDTO`)                             |
````

# HTTP Semantics

````
| Code | Meaning                                                                     |
| ---- | --------------------------------------------------------------------------- |
| 200  | OK (with JSON)                                                              |
| 201  | Created (with JSON)                                                         |
| 204  | No Content (deletion)                                                       |
| 400  | Bad Request (validation)                                                    |
| 404  | Not Found                                                                   |
| 500  | Internal Server Error (unexpected exceptions logged; 4xx for client errors) |
````


# Repository

GitHub: https://github.com/Phils1993/ExamFall2025