# Upgrade Spring Boot to 4.0.1 and JDK to 25

## Summary

Upgrade the ABtract platform from Spring Boot 3.3.13 to 4.0.1 and from Java 17 to Java 25 (JDK 25). This major version upgrade brings significant performance improvements, new features, and long-term support benefits.

## Motivation

### Why Spring Boot 4.0.1?
- **Performance Enhancements**: Spring Boot 4.x includes significant optimizations for startup time and memory usage
- **Native Image Support**: Better GraalVM native image compilation support
- **Modern Jakarta EE**: Uses Jakarta EE 10 namespaces consistently
- **Security Updates**: Latest security patches and vulnerability fixes
- **Feature Parity**: Aligns with the latest Spring ecosystem innovations

### Why JDK 25?
- **Long-Term Support**: JDK 25 is an LTS release with extended support
- **Performance Improvements**: Enhanced JIT compiler, garbage collection improvements
- **Language Features**: Access to latest Java language enhancements (pattern matching, records, etc.)
- **Virtual Threads**: Production-ready virtual threads for better concurrency
- **Memory Management**: Improved generational ZGC and better memory efficiency

## Current State

**Versions:**
- Spring Boot: 3.3.13
- Java: 17 (VERSION_17)
- Gradle: 8.1.1
- Lombok: 1.18.26
- Reactor: 3.5.5

## Target State

**Versions:**
- Spring Boot: 4.0.1
- Java: 25 (VERSION_25)
- Gradle: 8.11 (minimum for JDK 25 support)
- Lombok: 1.18.36 (compatible with JDK 25)
- Reactor: 3.7.x (via Spring Boot 4.0.1 BOM)

## Scope

### Direct Changes

**Build Configuration:**
- Update `springBootVersion` to `4.0.1` in root `build.gradle`
- Update Java source/target compatibility to `VERSION_25`
- Update Gradle wrapper to 8.11+
- Update dependency management plugin to 1.1.8+
- Update Lombok version to 1.18.36+

**Code Changes:**
- Replace any deprecated Spring Boot 3.x APIs with 4.x equivalents
- Update Jakarta EE namespace imports if any remain from older versions
- Verify reactive streams usage for compatibility
- Check for any blocking operations that could benefit from virtual threads

### Potential Breaking Changes

**Spring Boot 3.3 → 4.0:**
- Jakarta EE namespace migration (should already be complete in 3.3)
- Property configuration changes
- Actuator endpoint modifications
- Auto-configuration refinements

**Java 17 → 25:**
- Lombok compatibility requires version 1.18.34 or higher
- Some reflection-based patterns may need adjustment
- Module system improvements may affect dependencies

## Impact Analysis

### Breaking Changes

**Runtime:**
- Minimum JDK requirement changes from 17 to 25
- Deployment environments must support JDK 25

**Build:**
- Gradle 8.11+ required for JDK 25 compilation
- CI/CD pipelines must use JDK 25 build agents

**Dependencies:**
- Transitive dependencies will update via Spring Boot 4.0.1 BOM
- Some third-party libraries may need compatibility verification

### Non-Breaking Changes

- All existing AB testing platform features remain functional
- API contracts unchanged
- Database schemas unchanged
- Frontend (Vue 3) unaffected

## Alternatives Considered

1. **Upgrade to Spring Boot 3.5 only** - Rejected due to missing major improvements in 4.0
2. **Wait for Spring Boot 4.1** - Rejected as 4.0.1 is stable and production-ready
3. **Stay on Java 21** - Rejected as JDK 25 LTS provides better long-term support
4. **Gradual migration path** - Not feasible; requires full version bump

## Risks

**Medium Risk:**

1. **Dependency Compatibility**
   - Some transitive dependencies may not support JDK 25
   - Mitigation: Test thoroughly, update problematic dependencies

2. **Build Tooling**
   - Gradle version requirements may affect CI/CD
   - Mitigation: Update build agents early, test in staging environment

3. **Runtime Performance**
   - New JDK version may have unknown performance characteristics
   - Mitigation: Load testing in pre-production environment

4. **Lombok Compatibility**
   - Annotation processing changes in JDK 25
   - Mitigation: Use latest Lombok version, verify annotation processing

**Low Risk:**
- Spring Boot 4.0.1 is a stable release with extensive testing
- JDK 25 is an LTS release with backward compatibility focus
- Reactive codebase (WebFlux) is less affected by platform changes

## Success Criteria

1. ✅ All modules compile successfully with JDK 25
2. ✅ All unit and integration tests pass
3. ✅ Application starts and runs without errors
4. ✅ Build completes successfully with updated Gradle version
5. ✅ No regression in existing functionality
6. ✅ Performance is maintained or improved
7. ✅ CI/CD pipeline builds successfully

## Related Changes

None - this is a standalone upgrade effort

## Open Questions

Before proceeding, please confirm:

1. ✅ Are all CI/CD build agents capable of running JDK 25?
2. ✅ Do all deployment environments support JDK 25?
3. ✅ Should we create a feature branch for this upgrade or work directly on `dev`?
4. ✅ Are there any third-party dependencies that need verification for JDK 25 compatibility?
5. ✅ Should we enable virtual threads for any components?

## Estimated Effort

- Build configuration updates: 2-4 hours
- Dependency compatibility testing: 4-8 hours
- Code adjustments (if needed): 4-12 hours
- Testing and validation: 8-16 hours
- Documentation updates: 2-4 hours

**Total Estimate**: 20-44 hours (2.5-5.5 working days)

## References

- [Spring Boot 4.0.1 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes)
- [JDK 25 Release Notes](https://openjdk.org/projects/jdk/25/)
- [Spring Boot 4.0 Migration Guide](https://docs.spring.io/spring-boot/docs/4.0.1/reference/html/#migration)
