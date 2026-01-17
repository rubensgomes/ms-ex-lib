# CLAUDE.md

This file provides guidance for Claude Code (claude.ai/code) when working with this repository.

## Project Overview

**ms-ex-lib** is a Kotlin library providing standardized application exception classes for microservices. It defines a hierarchy of exceptions for different error categories: business, security, and system errors.

## Build Commands

```bash
# Clean build
./gradlew --info clean

# Apply code formatting (Spotless)
./gradlew :lib:spotlessApply

# Build the project
./gradlew --info build

# Run tests only
./gradlew --info test

# Check code style
./gradlew --info check

# Create JAR
./gradlew --info jar
```

## Project Structure

```
ms-ex-lib/
├── lib/                          # Main library module
│   ├── src/main/kotlin/          # Source code
│   │   └── com/rubensgomes/msexlib/
│   │       ├── ApplicationException.kt      # Base exception class
│   │       ├── business/
│   │       │   └── BusinessException.kt     # Business logic errors
│   │       ├── security/
│   │       │   └── SecurityException.kt     # Auth/access errors
│   │       └── system/
│   │           └── SystemException.kt       # Infrastructure errors
│   └── src/test/kotlin/          # Test code
├── build.gradle                  # Root build file
├── settings.gradle.kts           # Settings with version catalog
└── lib/build.gradle.kts          # Library build configuration
```

## Key Dependencies

- **ms-base-lib**: Contains `ApplicationError`, `ApplicationErrorCode`, and `Status` interfaces/enums used by this library
- **Spring Web**: For `HttpStatus` enum
- **Jakarta Validation**: For validation annotations
- **JUnit Jupiter**: For testing

## Exception Class Hierarchy

All exceptions extend `ApplicationException` which requires:
- `httpStatus: HttpStatus` - Must be an error status (4xx or 5xx)
- `status: Status` - Cannot be `Status.SUCCESS`
- `error: ApplicationError` - Structured error details
- `message: String` - Cannot be blank
- `cause: Throwable?` - Optional underlying cause

## Testing Notes

When writing tests for exceptions:

1. **`ApplicationError` is an interface** - Create anonymous implementations:
```kotlin
private fun createTestError(code: String, description: String): ApplicationError =
    object : ApplicationError {
      private var nativeErrorText: String? = null
      override fun getErrorDescription(): String = description
      override fun getNativeErrorText(): String? = nativeErrorText
      override fun setNativeErrorText(text: String?) { nativeErrorText = text }
      override fun getErrorCode(): ApplicationErrorCode =
          object : ApplicationErrorCode {
            override fun getCode(): String = code
            override fun getDescription(): String = description
          }
    }
```

2. **Always include `cause` parameter** when constructing exceptions (use `null` if no cause)

## Code Style

- Uses **Spotless** with **ktfmt** for Kotlin formatting
- All source files require Apache 2.0 license header
- Run `./gradlew :lib:spotlessApply` before committing

## Build Configuration

- **Java 25** (Amazon Corretto toolchain)
- **Kotlin** with strict JSR-305 null-safety
- **Gradle 9.1.0** with Kotlin DSL
- Published to GitHub Packages at `https://maven.pkg.github.com/rubensgomes/jvm-libs`
