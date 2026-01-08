<!-- OPENSPEC:START -->
# OpenSpec Instructions

These instructions are for AI assistants working in this project.

Always open `@/openspec/AGENTS.md` when the request:
- Mentions planning or proposals (words like proposal, spec, change, plan)
- Introduces new capabilities, breaking changes, architecture shifts, or big performance/security work
- Sounds ambiguous and you need the authoritative spec before coding

Use `@/openspec/AGENTS.md` to learn:
- How to create and apply change proposals
- Spec format and conventions
- Project structure and guidelines

Keep this managed block so 'openspec update' can refresh the instructions.

<!-- OPENSPEC:END -->

# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ABstract is an AB testing scaffold platform built with Spring Boot 4.0 (WebFlux + RSocket) and Couchbase/PostgreSQL (R2DBC). It provides feature management, experiment lifecycle management, and unified C-end interfaces for AB testing and fixed configurations.

**Tech Stack:**
- Java 21, Gradle 8.14.1
- Spring Boot 4.0.1, Spring WebFlux, Spring RSocket
- Couchbase (default) / PostgreSQL via R2DBC
- Frontend: Vue 3 + TypeScript + Element Plus + Vite
- Quality: Checkstyle, PMD, OWASP Dependency Check, JUnit 6 + TestContainer

⚠️ **Test Compatibility Notice:** Tests are currently disabled due to JUnit 6 + Mockito incompatibility. See [TEST_COMPATIBILITY.md](TEST_COMPATIBILITY.md) for details.

## Build & Test Commands

**Build all modules (without tests):**
```shell
./gradlew build -x test --no-parallel
```

⚠️ **Note:** Tests are currently disabled due to JUnit 6 + Mockito compatibility issues. See [TEST_COMPATIBILITY.md](TEST_COMPATIBILITY.md) for details.

**Compile source code:**
```shell
./gradlew compileJava --no-parallel
```

**Run backend locally (with docker-compose auto-start for Couchbase):**
```shell
./gradlew bootrun
```

**Build frontend:**
```shell
./gradlew npmBuild
```

**Run frontend dev server:**
```shell
cd frontend && pnpm dev
```

**Quality checks (auto-run on build):**
- Checkstyle: `./gradlew checkstyleMain checkstyleTest`
- PMD: `./gradlew pmdMain pmdTest`

## Module Architecture

The project follows **Domain-Driven Design (DDD)** with strict layer separation:

```
domain/          # Domain models and interfaces (no framework dependencies)
  ├─ core/       # Shared domain types (CustomerId, FilterCondition, etc.)
  ├─ feature/    # Feature and FeatureConfig domain models
  ├─ experiment/ # Experiment domain models
  ├─ customer/   # Customer domain models
  ├─ ai/         # AI integration domain
  └─ filterCondition/ # Filter condition domain

persistent/      # Infrastructure implementations
  ├─ core/                    # Couchbase-based implementations
  ├─ core-couchbase/          # Couchbase repositories
  ├─ core-r2dbc/              # R2DBC-based implementations
  ├─ feature/                 # Feature Couchbase persistence
  ├─ feature-r2dbc/           # Feature R2DBC persistence
  ├─ experiment/              # Experiment Couchbase persistence
  ├─ experiment-r2dbc/        # Experiment R2DBC persistence
  ├─ customer/                # Customer Couchbase persistence
  ├─ filterCondition/         # Filter condition persistence
  ├─ ai-openai/               # OpenAI integration
  ├─ ai-zhipu/                # Zhipu AI integration
  └─ ai-ollama/               # Ollama integration

api/             # REST/RSocket controllers (thin layer)
  ├─ core/       # Global error handler, logging, DTOs
  ├─ feature/    # Feature REST endpoints
  ├─ experiment/ # Experiment REST endpoints
  ├─ customer/   # Customer REST endpoints
  ├─ ai/         # AI REST endpoints
  ├─ file/       # File upload endpoints
  └─ filterCondition/ # Filter condition endpoints

main/            # Main Spring Boot application (Couchbase)
main-r2dbc/      # Alternative R2DBC-based application
frontend/        # Vue 3 frontend
```

## Key Architectural Patterns

**1. Interface-Based Design**
- Domain modules define interfaces (e.g., `domain/feature/Features.java`)
- Persistent modules provide implementations (e.g., `persistent/feature/FeaturesImpl.java`)
- API modules wire dependencies together

**2. Reactive Throughout**
- All database operations use Reactive Streams (Mono/Flux)
- Controllers return Mono<Flux> types
- No blocking operations in the request path

**3. Aggregate Pattern**
- Feature is an aggregate root containing FeatureConfigs
- Access configs through the feature: `feature.getConfigs().add(...)`
- See `api/feature/FeatureApi.java:133` for pattern example

**4. Dual Persistence Support**
- Couchbase: Default persistence (`main/`, `persistent/*-couchbase/`)
- R2DBC: Alternative PostgreSQL persistence (`main-r2dbc/`, `persistent/*-r2dbc/`)
- Domain interfaces are persistence-agnostic

## Configuration Profiles

**Default (Couchbase):**
```shell
./gradlew bootrun
```

**Zhipu AI:**
```shell
./gradlew bootrun --args='--spring.profiles.active=zhipu'
```

**Ollama:**
```shell
./gradlew bootrun --args='--spring.profiles.active=ollama'
```

## RSocket Testing (C-end Interface)

Use RSC (RSocket Client CLI) for testing RSocket endpoints:

```shell
# Request-Stream: Get feature configs for a customer
rsc --route=customers.featureConfigs.{featureKey} --stream \
  -m '{"customerId": "111","clientId":"610101","attributes":{}}' \
  --mmt application/json tcp://localhost:7001

# Request-Channel: Bidirectional streaming
rsc --route=customers.featureConfigs --channel \
  -m '{"customerId": "111","clientId":"610101","attributes":{}}' \
  --mmt application/json --data=- tcp://localhost:7001
```

## TestContainer Setup (Colima)

If using Colima as Docker provider, set these environment variables:

```shell
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/Users/<user>/.colima/docker.sock
export DOCKER_HOST=unix:///{rootpath}/.colima/docker.sock
export TESTCONTAINERS_RYUK_DISABLED=true
```

Add to `~/.bash_profile` and source in `~/.zshrc`.

## Development Guidelines

**Adding a New Feature:**
1. Define domain model in `domain/<feature>/`
2. Create domain interface in `domain/<feature>/` (e.g., `Features.java`)
3. Implement persistence in `persistent/<feature>/` or `persistent/<feature>-r2dbc/`
4. Add REST controller in `api/<feature>/`
5. Wire dependencies in `main/build.gradle` or `main-r2dbc/build.gradle`

**Code Organization:**
- Domain logic belongs in `domain/` modules
- Persistence details stay in `persistent/` modules
- API layer is thin - just request handling and response mapping
- Use reactive types (Mono/Flux) throughout

**Package Structure:**
- Java source: `cn.dpc.ab.<module>.<domain|persistent|api>`
- Domain types in `domain/<module>/src/main/java/cn/dpc/ab/domain/<module>/`
- API DTOs in `api/<module>/src/main/java/cn/dpc/ab/api/<module>/dto/`