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
package com.rubensgomes.ex.security

import com.rubensgomes.ex.ApplicationException
import com.rubensgomes.reqresp.dto.Error
import com.rubensgomes.reqresp.dto.ErrorCode
import com.rubensgomes.reqresp.dto.Status
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus

/**
 * Unit tests for [SecurityException] class.
 *
 * Tests cover inheritance from ApplicationException, security-specific error handling, and
 * validation logic specific to security domain errors.
 */
class SecurityExceptionTest {

  private val logger = LoggerFactory.getLogger(SecurityExceptionTest::class.java)

  // Mock implementation of Error interface for testing
  private class TestError(
      private var _nativeErrorText: String? = null,
      private val errorDescription: String = "Test security error description"
  ) : Error {

    override fun getErrorDescription(): String = errorDescription

    override fun getNativeErrorText(): String? = _nativeErrorText

    override fun setNativeErrorText(nativeErrorText: String?) {
      this._nativeErrorText = nativeErrorText
    }

    override fun getErrorCode(): ErrorCode {
      return object : ErrorCode {
        override fun getCode(): String = "SECURITY_ERROR"

        override fun getDescription(): String = "Security error code"
      }
    }
  }

  @Test
  fun constructorShouldCreateSecurityExceptionWithValidParameters() {
    logger.info("Testing SecurityException constructor with valid parameters")
    // Given
    val httpStatus = HttpStatus.UNAUTHORIZED
    val status = Status.ERROR
    val error = TestError("Authentication failed")
    val message = "Invalid credentials provided"
    val cause = RuntimeException("Token validation failed")
    logger.debug(
        "Test inputs: httpStatus={}, status={}, message='{}', cause='{}'",
        httpStatus,
        status,
        message,
        cause.message)

    // When
    logger.debug("Creating SecurityException instance")
    val exception = SecurityException(httpStatus, status, error, message, cause)

    // Then
    logger.debug("Verifying exception properties")
    assertEquals(httpStatus, exception.httpStatus)
    assertEquals(status, exception.status)
    assertEquals(error, exception.error)
    assertEquals(message, exception.message)
    assertEquals(cause, exception.cause)
    logger.info("✓ SecurityException created successfully with valid parameters")
  }

  @Test
  fun securityExceptionShouldExtendApplicationException() {
    logger.info("Testing SecurityException inheritance from ApplicationException")
    // Given
    val httpStatus = HttpStatus.FORBIDDEN
    val status = Status.ERROR
    val error = TestError()
    val message = "Access denied"
    val cause = RuntimeException("Insufficient permissions")
    logger.debug(
        "Test inputs: httpStatus={}, message='{}', cause='{}'", httpStatus, message, cause.message)

    // When
    logger.debug("Creating SecurityException instance")
    val exception = SecurityException(httpStatus, status, error, message, cause)

    // Then
    logger.debug("Verifying inheritance and type checking")
    assertTrue(exception is ApplicationException)
    assertTrue(exception is SecurityException)
    assertNotNull(exception as? Exception)
    logger.info("✓ SecurityException correctly extends ApplicationException")
  }

  @Test
  fun constructorShouldThrowWhenHttpStatusIsNotErrorStatus() {
    logger.info("Testing SecurityException constructor with non-error HTTP status")
    // Given
    val httpStatus = HttpStatus.OK // 200 - not an error status
    val status = Status.ERROR
    val error = TestError()
    val message = "Authentication failed"
    logger.debug(
        "Test inputs: httpStatus={} (non-error status), status={}, message='{}'",
        httpStatus,
        status,
        message)

    // When & Then
    logger.debug("Expecting IllegalArgumentException to be thrown")
    val exception =
        assertThrows<IllegalArgumentException> {
          SecurityException(httpStatus, status, error, message, null)
        }
    assertEquals("HTTP status must be an error status, got: $httpStatus", exception.message)
    logger.info(
        "✓ IllegalArgumentException correctly thrown for non-error HTTP status: {}", httpStatus)
  }

  @Test
  fun constructorShouldThrowWhenStatusIsSuccess() {
    logger.info("Testing SecurityException constructor with SUCCESS status")
    // Given
    val httpStatus = HttpStatus.UNAUTHORIZED
    val status = Status.SUCCESS // Not allowed for exceptions
    val error = TestError()
    val message = "Security violation"
    logger.debug(
        "Test inputs: httpStatus={}, status={} (not allowed for exceptions), message='{}'",
        httpStatus,
        status,
        message)

    // When & Then
    logger.debug("Expecting IllegalArgumentException to be thrown for SUCCESS status")
    val exception =
        assertThrows<IllegalArgumentException> {
          SecurityException(httpStatus, status, error, message, null)
        }
    assertEquals("Exception status cannot be SUCCESS, got: $status", exception.message)
    logger.info("✓ IllegalArgumentException correctly thrown for SUCCESS status")
  }

  @Test
  fun constructorShouldWorkWithCommonSecurityHttpStatuses() {
    logger.info("Testing SecurityException constructor with common security HTTP statuses")
    val securityHttpStatuses =
        listOf(
            HttpStatus.UNAUTHORIZED, // 401 - Authentication required
            HttpStatus.FORBIDDEN, // 403 - Access denied
            HttpStatus.TOO_MANY_REQUESTS, // 429 - Rate limiting
            HttpStatus.CONFLICT, // 409 - Account locked
            HttpStatus.GONE, // 410 - Account expired
            HttpStatus.PRECONDITION_FAILED // 412 - Security policy violation
            )
    logger.debug(
        "Testing with {} different security-related HTTP statuses", securityHttpStatuses.size)

    securityHttpStatuses.forEach { httpStatus ->
      // Given
      val status = Status.ERROR
      val error = TestError()
      val message = "Security error for $httpStatus"
      logger.trace("Testing with httpStatus: {}", httpStatus)

      // When & Then (should not throw)
      val exception = SecurityException(httpStatus, status, error, message, null)
      assertEquals(httpStatus, exception.httpStatus)
      logger.trace("✓ Successfully created SecurityException with httpStatus: {}", httpStatus)
    }
    logger.info(
        "✓ All {} security-related HTTP statuses handled correctly", securityHttpStatuses.size)
  }

  @Test
  fun constructorShouldSetNativeErrorTextFromSecurityCause() {
    logger.info("Testing native error text setting from security-related cause")
    // Given
    val httpStatus = HttpStatus.UNAUTHORIZED
    val status = Status.ERROR
    val error = TestError("") // blank nativeErrorText
    val message = "Authentication failed"
    val securityCause = RuntimeException("JWT token has expired")
    logger.debug(
        "Test inputs: httpStatus={}, error.nativeErrorText='{}' (blank), securityCause.message='{}'",
        httpStatus,
        error.getNativeErrorText(),
        securityCause.message)

    // When
    logger.debug("Creating SecurityException with blank error text and security cause")
    val exception = SecurityException(httpStatus, status, error, message, securityCause)

    // Then
    logger.debug("Verifying native error text was set from security cause")
    assertNotNull(exception)
    assertEquals("JWT token has expired", exception.error.nativeErrorText)
    logger.info(
        "✓ Native error text correctly set from security cause: '{}'",
        exception.error.nativeErrorText)
  }

  @Test
  fun constructorShouldPreserveExistingSecurityErrorText() {
    logger.info("Testing that existing security error text is preserved")
    // Given
    val httpStatus = HttpStatus.FORBIDDEN
    val status = Status.ERROR
    val originalSecurityError = "User lacks required security clearance"
    val error = TestError(originalSecurityError)
    val message = "Access denied to classified resource"
    val cause = RuntimeException("This should not override security error")
    logger.debug(
        "Test inputs: httpStatus={}, originalSecurityError='{}', cause.message='{}'",
        httpStatus,
        originalSecurityError,
        cause.message)

    // When
    logger.debug("Creating SecurityException with existing security error text")
    val exception = SecurityException(httpStatus, status, error, message, cause)

    // Then
    logger.debug("Verifying original security error text was preserved")
    assertEquals(originalSecurityError, exception.error.nativeErrorText)
    logger.info("✓ Original security error text preserved: '{}'", exception.error.nativeErrorText)
  }

  @Test
  fun constructorShouldHandleChainedSecurityExceptions() {
    logger.info("Testing SecurityException with chained security-related causes")
    // Given
    val httpStatus = HttpStatus.UNAUTHORIZED
    val status = Status.ERROR
    val error = TestError("") // blank nativeErrorText
    val message = "Multi-factor authentication failed"

    // Create a chain of security-related causes
    val rootCause = RuntimeException("TOTP verification failed")
    val authCause = IllegalStateException("SMS delivery timeout", rootCause)
    val validationCause = IllegalArgumentException("Invalid authentication code format", authCause)
    logger.debug(
        "Created security exception chain: {} -> {} -> {}",
        validationCause.javaClass.simpleName,
        authCause.javaClass.simpleName,
        rootCause.javaClass.simpleName)
    logger.debug("Root cause message: '{}'", rootCause.message)

    // When
    logger.debug("Creating SecurityException with security exception chain")
    val exception = SecurityException(httpStatus, status, error, message, validationCause)

    // Then
    logger.debug("Verifying root cause message was extracted from security chain")
    assertNotNull(exception)
    assertEquals("TOTP verification failed", exception.error.nativeErrorText)
    assertEquals(validationCause, exception.cause)
    logger.info(
        "✓ Root cause message correctly extracted from security exception chain: '{}'",
        exception.error.nativeErrorText)
  }

  @Test
  fun constructorShouldHandleSecurityExceptionWithoutCause() {
    logger.info("Testing SecurityException creation without cause")
    // Given
    val httpStatus = HttpStatus.TOO_MANY_REQUESTS
    val status = Status.ERROR
    val error = TestError()
    val message = "Rate limit exceeded"
    logger.debug(
        "Test inputs: httpStatus={}, status={}, message='{}', cause=null",
        httpStatus,
        status,
        message)

    // When
    logger.debug("Creating SecurityException without cause")
    val exception = SecurityException(httpStatus, status, error, message, null)

    // Then
    logger.debug("Verifying security exception created successfully without cause")
    assertNotNull(exception)
    assertEquals(error, exception.error)
    assertNull(exception.cause)
    assertEquals(message, exception.message)
    logger.info(
        "✓ SecurityException created successfully without cause, message: '{}'", exception.message)
  }

  @Test
  fun securityExceptionShouldMaintainSecurityContext() {
    logger.info("Testing SecurityException maintains security context and properties")
    // Given
    val httpStatus = HttpStatus.FORBIDDEN
    val status = Status.ERROR
    val error = TestError("Role-based access control violation")
    val securityMessage = "User does not have required role: ADMIN"
    val securityCause = RuntimeException("Permission check failed")
    logger.debug(
        "Security context: httpStatus={}, message='{}', cause='{}'",
        httpStatus,
        securityMessage,
        securityCause.message)

    // When
    logger.debug("Creating SecurityException with security context")
    val exception = SecurityException(httpStatus, status, error, securityMessage, securityCause)

    // Then
    logger.debug("Verifying security context is maintained")
    assertEquals(httpStatus, exception.httpStatus)
    assertEquals(status, exception.status)
    assertEquals(error, exception.error)
    assertEquals(securityMessage, exception.message)
    assertEquals(securityCause, exception.cause)

    // Verify it's still a security exception type
    assertTrue(exception is SecurityException)
    logger.info(
        "✓ SecurityException maintains all security context: status={}, message='{}'",
        exception.httpStatus,
        exception.message)
  }

  @Test
  fun constructorShouldHandleAuthenticationFailureScenarios() {
    logger.info("Testing SecurityException with various authentication failure scenarios")
    val authFailureScenarios =
        mapOf(
            HttpStatus.UNAUTHORIZED to "Invalid username or password",
            HttpStatus.UNAUTHORIZED to "Account credentials expired",
            HttpStatus.FORBIDDEN to "Account is locked due to multiple failed attempts",
            HttpStatus.GONE to "User account has been deactivated",
            HttpStatus.TOO_MANY_REQUESTS to "Too many login attempts, please try again later")
    logger.debug("Testing {} different authentication failure scenarios", authFailureScenarios.size)

    authFailureScenarios.forEach { (httpStatus, message) ->
      // Given
      val status = Status.ERROR
      val error = TestError("Authentication system error")
      val authCause = RuntimeException("Identity provider validation failed")
      logger.trace("Testing authentication scenario: {} - '{}'", httpStatus, message)

      // When & Then (should not throw)
      val exception = SecurityException(httpStatus, status, error, message, authCause)
      assertEquals(httpStatus, exception.httpStatus)
      assertEquals(message, exception.message)
      logger.trace("✓ Successfully handled authentication scenario: {}", httpStatus)
    }
    logger.info(
        "✓ All {} authentication failure scenarios handled correctly", authFailureScenarios.size)
  }

  @Test
  fun constructorShouldHandleAuthorizationFailureScenarios() {
    logger.info("Testing SecurityException with various authorization failure scenarios")
    val authzFailureScenarios =
        mapOf(
            HttpStatus.FORBIDDEN to "Insufficient privileges to access this resource",
            HttpStatus.FORBIDDEN to "User role does not permit this operation",
            HttpStatus.FORBIDDEN to "Resource access restricted by security policy",
            HttpStatus.PRECONDITION_FAILED to "Security clearance level insufficient")
    logger.debug("Testing {} different authorization failure scenarios", authzFailureScenarios.size)

    authzFailureScenarios.forEach { (httpStatus, message) ->
      // Given
      val status = Status.ERROR
      val error = TestError("Authorization system error")
      val authzCause = RuntimeException("Permission matrix check failed")
      logger.trace("Testing authorization scenario: {} - '{}'", httpStatus, message)

      // When & Then (should not throw)
      val exception = SecurityException(httpStatus, status, error, message, authzCause)
      assertEquals(httpStatus, exception.httpStatus)
      assertEquals(message, exception.message)
      logger.trace("✓ Successfully handled authorization scenario: {}", httpStatus)
    }
    logger.info(
        "✓ All {} authorization failure scenarios handled correctly", authzFailureScenarios.size)
  }
}
