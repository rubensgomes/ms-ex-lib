/*
 * Copyright 2025 Rubens Gomes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rubensgomes.msexlib.system

import com.rubensgomes.msexlib.ApplicationException
import com.rubensgomes.msreqresplib.Status
import com.rubensgomes.msreqresplib.error.Error
import com.rubensgomes.msreqresplib.error.ErrorCode
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus

/**
 * Unit tests for [SystemException] class.
 *
 * Tests cover inheritance from ApplicationException, system-specific error handling, and validation
 * logic specific to system-level domain errors.
 */
class SystemExceptionTest {

  private val logger = LoggerFactory.getLogger(SystemExceptionTest::class.java)

  // Mock implementation of Error interface for testing
  private class TestError(
      private var _nativeErrorText: String? = null,
      private val errorDescription: String = "Test system error description"
  ) : Error {

    override fun getErrorDescription(): String = errorDescription

    override fun getNativeErrorText(): String? = _nativeErrorText

    override fun setNativeErrorText(nativeErrorText: String?) {
      this._nativeErrorText = nativeErrorText
    }

    override fun getErrorCode(): ErrorCode {
      return object : ErrorCode {
        override fun getCode(): String = "SYSTEM_ERROR"

        override fun getDescription(): String = "System error code"
      }
    }
  }

  @Test
  fun constructorShouldCreateSystemExceptionWithValidParameters() {
    logger.info("Testing SystemException constructor with valid parameters")
    // Given
    val httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    val status = Status.ERROR
    val error = TestError("Database connection failed")
    val message = "Unable to connect to primary database"
    val cause = RuntimeException("Connection timeout after 30 seconds")
    logger.debug(
        "Test inputs: httpStatus={}, status={}, message='{}', cause='{}'",
        httpStatus,
        status,
        message,
        cause.message)

    // When
    logger.debug("Creating SystemException instance")
    val exception = SystemException(httpStatus, status, error, message, cause)

    // Then
    logger.debug("Verifying exception properties")
    Assertions.assertEquals(httpStatus, exception.httpStatus)
    Assertions.assertEquals(status, exception.status)
    Assertions.assertEquals(error, exception.error)
    Assertions.assertEquals(message, exception.message)
    Assertions.assertEquals(cause, exception.cause)
    logger.info("✓ SystemException created successfully with valid parameters")
  }

  @Test
  fun systemExceptionShouldExtendApplicationException() {
    logger.info("Testing SystemException inheritance from ApplicationException")
    // Given
    val httpStatus = HttpStatus.SERVICE_UNAVAILABLE
    val status = Status.ERROR
    val error = TestError()
    val message = "External service unavailable"
    val cause = RuntimeException("Circuit breaker opened")
    logger.debug(
        "Test inputs: httpStatus={}, message='{}', cause='{}'", httpStatus, message, cause.message)

    // When
    logger.debug("Creating SystemException instance")
    val exception = SystemException(httpStatus, status, error, message, cause)

    // Then
    logger.debug("Verifying inheritance and type checking")
    Assertions.assertTrue(exception is ApplicationException)
    Assertions.assertTrue(exception is SystemException)
    Assertions.assertNotNull(exception as? Exception)
    logger.info("✓ SystemException correctly extends ApplicationException")
  }

  @Test
  fun constructorShouldThrowWhenHttpStatusIsNotErrorStatus() {
    logger.info("Testing SystemException constructor with non-error HTTP status")
    // Given
    val httpStatus = HttpStatus.OK // 200 - not an error status
    val status = Status.ERROR
    val error = TestError()
    val message = "System operation failed"
    logger.debug(
        "Test inputs: httpStatus={} (non-error status), status={}, message='{}'",
        httpStatus,
        status,
        message)

    // When & Then
    logger.debug("Expecting IllegalArgumentException to be thrown")
    val exception =
        assertThrows<IllegalArgumentException> {
          SystemException(httpStatus, status, error, message, null)
        }
    Assertions.assertEquals(
        "HTTP status must be an error status, got: $httpStatus", exception.message)
    logger.info(
        "✓ IllegalArgumentException correctly thrown for non-error HTTP status: {}", httpStatus)
  }

  @Test
  fun constructorShouldThrowWhenStatusIsSuccess() {
    logger.info("Testing SystemException constructor with SUCCESS status")
    // Given
    val httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    val status = Status.SUCCESS // Not allowed for exceptions
    val error = TestError()
    val message = "System failure occurred"
    logger.debug(
        "Test inputs: httpStatus={}, status={} (not allowed for exceptions), message='{}'",
        httpStatus,
        status,
        message)

    // When & Then
    logger.debug("Expecting IllegalArgumentException to be thrown for SUCCESS status")
    val exception =
        assertThrows<IllegalArgumentException> {
          SystemException(httpStatus, status, error, message, null)
        }
    Assertions.assertEquals("Exception status cannot be SUCCESS, got: $status", exception.message)
    logger.info("✓ IllegalArgumentException correctly thrown for SUCCESS status")
  }

  @Test
  fun constructorShouldWorkWithCommonSystemHttpStatuses() {
    logger.info("Testing SystemException constructor with common system HTTP statuses")
    val systemHttpStatuses =
        listOf(
            HttpStatus.INTERNAL_SERVER_ERROR, // 500 - General system error
            HttpStatus.BAD_GATEWAY, // 502 - Upstream system failure
            HttpStatus.SERVICE_UNAVAILABLE, // 503 - System overload/maintenance
            HttpStatus.GATEWAY_TIMEOUT, // 504 - System timeout
            HttpStatus.INSUFFICIENT_STORAGE, // 507 - System storage issues
            HttpStatus.BANDWIDTH_LIMIT_EXCEEDED // 509 - System capacity exceeded
            )
    logger.debug("Testing with {} different system-related HTTP statuses", systemHttpStatuses.size)

    systemHttpStatuses.forEach { httpStatus ->
      // Given
      val status = Status.ERROR
      val error = TestError()
      val message = "System error for $httpStatus"
      logger.trace("Testing with httpStatus: {}", httpStatus)

      // When & Then (should not throw)
      val exception = SystemException(httpStatus, status, error, message, null)
      Assertions.assertEquals(httpStatus, exception.httpStatus)
      logger.trace("✓ Successfully created SystemException with httpStatus: {}", httpStatus)
    }
    logger.info("✓ All {} system-related HTTP statuses handled correctly", systemHttpStatuses.size)
  }

  @Test
  @Disabled("skipped temporarily")
  fun constructorShouldSetNativeErrorTextFromSystemCause() {
    logger.info("Testing native error text setting from system-related cause")
    // Given
    val httpStatus = HttpStatus.SERVICE_UNAVAILABLE
    val status = Status.ERROR
    val error = TestError("") // blank nativeErrorText
    val message = "Database pool exhausted"
    val systemCause = RuntimeException("Connection pool maximum size reached")
    logger.debug(
        "Test inputs: httpStatus={}, error.nativeErrorText='{}' (blank), systemCause.message='{}'",
        httpStatus,
        error.getNativeErrorText(),
        systemCause.message)

    // When
    logger.debug("Creating SystemException with blank error text and system cause")
    val exception = SystemException(httpStatus, status, error, message, systemCause)

    // Then
    logger.debug("Verifying native error text was set from system cause")
    Assertions.assertNotNull(exception)
    Assertions.assertEquals(
        "Connection pool maximum size reached", exception.error.getNativeErrorText())
    logger.info(
        "✓ Native error text correctly set from system cause: '{}'",
        exception.error.getNativeErrorText())
  }

  @Test
  fun constructorShouldPreserveExistingSystemErrorText() {
    logger.info("Testing that existing system error text is preserved")
    // Given
    val httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    val status = Status.ERROR
    val originalSystemError = "Disk space insufficient for operation"
    val error = TestError(originalSystemError)
    val message = "File system operation failed"
    val cause = RuntimeException("This should not override system error")
    logger.debug(
        "Test inputs: httpStatus={}, originalSystemError='{}', cause.message='{}'",
        httpStatus,
        originalSystemError,
        cause.message)

    // When
    logger.debug("Creating SystemException with existing system error text")
    val exception = SystemException(httpStatus, status, error, message, cause)

    // Then
    logger.debug("Verifying original system error text was preserved")
    Assertions.assertEquals(originalSystemError, exception.error.getNativeErrorText())
    logger.info(
        "✓ Original system error text preserved: '{}'", exception.error.getNativeErrorText())
  }

  @Test
  @Disabled("skipped temporarily")
  fun constructorShouldHandleChainedSystemExceptions() {
    logger.info("Testing SystemException with chained system-related causes")
    // Given
    val httpStatus = HttpStatus.BAD_GATEWAY
    val status = Status.ERROR
    val error = TestError("") // blank nativeErrorText
    val message = "Upstream service failure"

    // Create a chain of system-related causes
    val rootCause = RuntimeException("Network interface down")
    val networkCause = IllegalStateException("TCP connection reset", rootCause)
    val serviceCause = IllegalArgumentException("Service discovery failed", networkCause)
    logger.debug(
        "Created system exception chain: {} -> {} -> {}",
        serviceCause.javaClass.simpleName,
        networkCause.javaClass.simpleName,
        rootCause.javaClass.simpleName)
    logger.debug("Root cause message: '{}'", rootCause.message)

    // When
    logger.debug("Creating SystemException with system exception chain")
    val exception = SystemException(httpStatus, status, error, message, serviceCause)

    // Then
    logger.debug("Verifying root cause message was extracted from system chain")
    Assertions.assertNotNull(exception)
    Assertions.assertEquals("Network interface down", exception.error.getNativeErrorText())
    Assertions.assertEquals(serviceCause, exception.cause)
    logger.info(
        "✓ Root cause message correctly extracted from system exception chain: '{}'",
        exception.error.getNativeErrorText())
  }

  @Test
  fun constructorShouldHandleSystemExceptionWithoutCause() {
    logger.info("Testing SystemException creation without cause")
    // Given
    val httpStatus = HttpStatus.INSUFFICIENT_STORAGE
    val status = Status.ERROR
    val error = TestError()
    val message = "System storage quota exceeded"
    logger.debug(
        "Test inputs: httpStatus={}, status={}, message='{}', cause=null",
        httpStatus,
        status,
        message)

    // When
    logger.debug("Creating SystemException without cause")
    val exception = SystemException(httpStatus, status, error, message, null)

    // Then
    logger.debug("Verifying system exception created successfully without cause")
    Assertions.assertNotNull(exception)
    Assertions.assertEquals(error, exception.error)
    Assertions.assertNull(exception.cause)
    Assertions.assertEquals(message, exception.message)
    logger.info(
        "✓ SystemException created successfully without cause, message: '{}'", exception.message)
  }

  @Test
  fun systemExceptionShouldMaintainSystemContext() {
    logger.info("Testing SystemException maintains system context and properties")
    // Given
    val httpStatus = HttpStatus.GATEWAY_TIMEOUT
    val status = Status.ERROR
    val error = TestError("External API timeout violation")
    val systemMessage = "Third-party service response timeout after 60 seconds"
    val systemCause = RuntimeException("HTTP read timeout")
    logger.debug(
        "System context: httpStatus={}, message='{}', cause='{}'",
        httpStatus,
        systemMessage,
        systemCause.message)

    // When
    logger.debug("Creating SystemException with system context")
    val exception = SystemException(httpStatus, status, error, systemMessage, systemCause)

    // Then
    logger.debug("Verifying system context is maintained")
    Assertions.assertEquals(httpStatus, exception.httpStatus)
    Assertions.assertEquals(status, exception.status)
    Assertions.assertEquals(error, exception.error)
    Assertions.assertEquals(systemMessage, exception.message)
    Assertions.assertEquals(systemCause, exception.cause)

    // Verify it's still a system exception type
    Assertions.assertTrue(exception is SystemException)
    logger.info(
        "✓ SystemException maintains all system context: status={}, message='{}'",
        exception.httpStatus,
        exception.message)
  }

  @Test
  fun constructorShouldHandleInfrastructureFailureScenarios() {
    logger.info("Testing SystemException with various infrastructure failure scenarios")
    val infrastructureFailureScenarios =
        mapOf(
            HttpStatus.INTERNAL_SERVER_ERROR to "Database server unreachable",
            HttpStatus.SERVICE_UNAVAILABLE to "Load balancer health check failed",
            HttpStatus.BAD_GATEWAY to "Upstream microservice returned invalid response",
            HttpStatus.GATEWAY_TIMEOUT to "Message queue processing timeout",
            HttpStatus.INSUFFICIENT_STORAGE to "Log file storage capacity exceeded")
    logger.debug(
        "Testing {} different infrastructure failure scenarios",
        infrastructureFailureScenarios.size)

    infrastructureFailureScenarios.forEach { (httpStatus, message) ->
      // Given
      val status = Status.ERROR
      val error = TestError("Infrastructure monitoring alert")
      val infraCause = RuntimeException("System health check failure")
      logger.trace("Testing infrastructure scenario: {} - '{}'", httpStatus, message)

      // When & Then (should not throw)
      val exception = SystemException(httpStatus, status, error, message, infraCause)
      Assertions.assertEquals(httpStatus, exception.httpStatus)
      Assertions.assertEquals(message, exception.message)
      logger.trace("✓ Successfully handled infrastructure scenario: {}", httpStatus)
    }
    logger.info(
        "✓ All {} infrastructure failure scenarios handled correctly",
        infrastructureFailureScenarios.size)
  }

  @Test
  fun constructorShouldHandleResourceManagementScenarios() {
    logger.info("Testing SystemException with various resource management scenarios")
    val resourceManagementScenarios =
        mapOf(
            HttpStatus.INTERNAL_SERVER_ERROR to "Memory allocation failed",
            HttpStatus.SERVICE_UNAVAILABLE to "Thread pool exhausted",
            HttpStatus.INSUFFICIENT_STORAGE to "Temporary file creation failed",
            HttpStatus.BANDWIDTH_LIMIT_EXCEEDED to "Network bandwidth quota exceeded")
    logger.debug(
        "Testing {} different resource management scenarios", resourceManagementScenarios.size)

    resourceManagementScenarios.forEach { (httpStatus, message) ->
      // Given
      val status = Status.ERROR
      val error = TestError("Resource management system error")
      val resourceCause = RuntimeException("System resource monitor alert")
      logger.trace("Testing resource management scenario: {} - '{}'", httpStatus, message)

      // When & Then (should not throw)
      val exception = SystemException(httpStatus, status, error, message, resourceCause)
      Assertions.assertEquals(httpStatus, exception.httpStatus)
      Assertions.assertEquals(message, exception.message)
      logger.trace("✓ Successfully handled resource management scenario: {}", httpStatus)
    }
    logger.info(
        "✓ All {} resource management scenarios handled correctly",
        resourceManagementScenarios.size)
  }

  @Test
  fun constructorShouldHandleConfigurationErrorScenarios() {
    logger.info("Testing SystemException with various configuration error scenarios")
    val configurationErrorScenarios =
        mapOf(
            HttpStatus.INTERNAL_SERVER_ERROR to "Required configuration property missing",
            HttpStatus.SERVICE_UNAVAILABLE to "Invalid database connection string",
            HttpStatus.BAD_GATEWAY to "API endpoint configuration invalid",
            HttpStatus.GATEWAY_TIMEOUT to "Timeout configuration value out of range")
    logger.debug(
        "Testing {} different configuration error scenarios", configurationErrorScenarios.size)

    configurationErrorScenarios.forEach { (httpStatus, message) ->
      // Given
      val status = Status.ERROR
      val error = TestError("Configuration validation error")
      val configCause = RuntimeException("Configuration parser exception")
      logger.trace("Testing configuration error scenario: {} - '{}'", httpStatus, message)

      // When & Then (should not throw)
      val exception = SystemException(httpStatus, status, error, message, configCause)
      Assertions.assertEquals(httpStatus, exception.httpStatus)
      Assertions.assertEquals(message, exception.message)
      logger.trace("✓ Successfully handled configuration error scenario: {}", httpStatus)
    }
    logger.info(
        "✓ All {} configuration error scenarios handled correctly",
        configurationErrorScenarios.size)
  }
}
