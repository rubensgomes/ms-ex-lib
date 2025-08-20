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
package com.rubensgomes.ex

import com.rubensgomes.reqresp.dto.Error
import com.rubensgomes.reqresp.dto.ErrorCode
import com.rubensgomes.reqresp.dto.Status
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus

/**
 * Unit tests for [ApplicationException] class.
 *
 * Tests cover validation logic, constructor behavior, and error text handling.
 */
class ApplicationExceptionTest {

  private val logger = LoggerFactory.getLogger(ApplicationExceptionTest::class.java)

  // Mock implementation of Error interface for testing
  private class TestError(
      private var _nativeErrorText: String? = null,
      private val errorDescription: String = "Test error description"
  ) : Error {

    override fun getErrorDescription(): String = errorDescription

    override fun getNativeErrorText(): String? = _nativeErrorText

    override fun setNativeErrorText(nativeErrorText: String?) {
      this._nativeErrorText = nativeErrorText
    }

    override fun getErrorCode(): ErrorCode {
      return object : ErrorCode {
        override fun getCode(): String = "TEST_ERROR"

        override fun getDescription(): String = "Test error code"
      }
    }
  }

  @Test
  fun constructorShouldCreateExceptionWithValidParameters() {
    logger.info("Testing ApplicationException constructor with valid parameters")
    // Given
    val httpStatus = HttpStatus.BAD_REQUEST
    val status = Status.ERROR
    val error = TestError("Test error")
    val message = "Test exception message"
    val cause = RuntimeException("Root cause")
    logger.debug(
        "Test inputs: httpStatus={}, status={}, message='{}', cause='{}'",
        httpStatus,
        status,
        message,
        cause.message)

    // When
    logger.debug("Creating ApplicationException instance")
    val exception = ApplicationException(httpStatus, status, error, message, cause)

    // Then
    logger.debug("Verifying exception properties")
    assertEquals(httpStatus, exception.httpStatus)
    assertEquals(status, exception.status)
    assertEquals(error, exception.error)
    assertEquals(message, exception.message)
    assertEquals(cause, exception.cause)
    logger.info("✓ ApplicationException created successfully with valid parameters")
  }

  @Test
  fun constructorShouldThrowWhenHttpStatusIsNotErrorStatus() {
    logger.info("Testing ApplicationException constructor with non-error HTTP status")
    // Given
    val httpStatus = HttpStatus.OK // 200 - not an error status
    val status = Status.ERROR
    val error = TestError()
    val message = "Test message"
    logger.debug(
        "Test inputs: httpStatus={} (non-error status), status={}, message='{}'",
        httpStatus,
        status,
        message)

    // When & Then
    logger.debug("Expecting IllegalArgumentException to be thrown")
    val exception =
        assertThrows<IllegalArgumentException> {
          ApplicationException(httpStatus, status, error, message, null)
        }
    assertEquals("HTTP status must be an error status, got: $httpStatus", exception.message)
    logger.info(
        "✓ IllegalArgumentException correctly thrown for non-error HTTP status: {}", httpStatus)
  }

  @Test
  fun constructorShouldThrowWhenMessageIsBlank() {
    logger.info("Testing ApplicationException constructor with blank message")
    // Given
    val httpStatus = HttpStatus.BAD_REQUEST
    val status = Status.ERROR
    val error = TestError()
    val message = ""
    logger.debug("Test inputs: httpStatus={}, status={}, message='{}'", httpStatus, status, message)

    // When & Then
    logger.debug("Expecting IllegalArgumentException to be thrown for blank message")
    val exception =
        assertThrows<IllegalArgumentException> {
          ApplicationException(httpStatus, status, error, message, null)
        }
    assertEquals("Exception message must not be blank", exception.message)
    logger.info("✓ IllegalArgumentException correctly thrown for blank message")
  }

  @Test
  fun constructorShouldThrowWhenMessageIsWhitespaceOnly() {
    logger.info("Testing ApplicationException constructor with whitespace-only message")
    // Given
    val httpStatus = HttpStatus.BAD_REQUEST
    val status = Status.ERROR
    val error = TestError()
    val message = "   "
    logger.debug(
        "Test inputs: httpStatus={}, status={}, message='{}' (whitespace only)",
        httpStatus,
        status,
        message)

    // When & Then
    logger.debug("Expecting IllegalArgumentException to be thrown for whitespace-only message")
    val exception =
        assertThrows<IllegalArgumentException> {
          ApplicationException(httpStatus, status, error, message, null)
        }
    assertEquals("Exception message must not be blank", exception.message)
    logger.info("✓ IllegalArgumentException correctly thrown for whitespace-only message")
  }

  @Test
  fun constructorShouldThrowWhenStatusIsSuccess() {
    logger.info("Testing ApplicationException constructor with SUCCESS status")
    // Given
    val httpStatus = HttpStatus.BAD_REQUEST
    val status = Status.SUCCESS // Not allowed for exceptions
    val error = TestError()
    val message = "Test message"
    logger.debug(
        "Test inputs: httpStatus={}, status={} (not allowed for exceptions), message='{}'",
        httpStatus,
        status,
        message)

    // When & Then
    logger.debug("Expecting IllegalArgumentException to be thrown for SUCCESS status")
    val exception =
        assertThrows<IllegalArgumentException> {
          ApplicationException(httpStatus, status, error, message, null)
        }
    assertEquals("Exception status cannot be SUCCESS, got: $status", exception.message)
    logger.info("✓ IllegalArgumentException correctly thrown for SUCCESS status")
  }

  @Test
  fun constructorShouldWorkWithVariousErrorHttpStatuses() {
    logger.info("Testing ApplicationException constructor with various error HTTP statuses")
    val errorStatuses =
        listOf(
            HttpStatus.BAD_REQUEST,
            HttpStatus.UNAUTHORIZED,
            HttpStatus.FORBIDDEN,
            HttpStatus.NOT_FOUND,
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatus.BAD_GATEWAY,
            HttpStatus.SERVICE_UNAVAILABLE)
    logger.debug("Testing with {} different error HTTP statuses", errorStatuses.size)

    errorStatuses.forEach { httpStatus ->
      // Given
      val status = Status.ERROR
      val error = TestError()
      val message = "Test message for $httpStatus"
      logger.trace("Testing with httpStatus: {}", httpStatus)

      // When & Then (should not throw)
      val exception = ApplicationException(httpStatus, status, error, message, null)
      assertEquals(httpStatus, exception.httpStatus)
      logger.trace("✓ Successfully created exception with httpStatus: {}", httpStatus)
    }
    logger.info("✓ All {} error HTTP statuses handled correctly", errorStatuses.size)
  }

  @Test
  fun constructorShouldSetNativeErrorTextWhenErrorHasBlankTextAndCauseExists() {
    logger.info("Testing native error text setting when error has blank text and cause exists")
    // Given
    val httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    val status = Status.ERROR
    val error = TestError("") // blank nativeErrorText
    val message = "Test message"
    val rootCause = RuntimeException("Root cause message")
    logger.debug(
        "Test inputs: httpStatus={}, error.nativeErrorText='{}' (blank), rootCause.message='{}'",
        httpStatus,
        error.getNativeErrorText(),
        rootCause.message)

    // When
    logger.debug("Creating ApplicationException with blank error text and cause")
    val exception = ApplicationException(httpStatus, status, error, message, rootCause)

    // Then
    logger.debug("Verifying native error text was set from root cause")
    assertNotNull(exception)
    assertEquals("Root cause message", exception.error.nativeErrorText)
    logger.info(
        "✓ Native error text correctly set from root cause: '{}'", exception.error.nativeErrorText)
  }

  @Test
  fun constructorShouldSetNativeErrorTextWhenErrorHasNullTextAndCauseExists() {
    logger.info("Testing native error text setting when error has null text and cause exists")
    // Given
    val httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    val status = Status.ERROR
    val error = TestError(null) // null nativeErrorText
    val message = "Test message"
    val rootCause = RuntimeException("Root cause message")
    logger.debug(
        "Test inputs: httpStatus={}, error.nativeErrorText=null, rootCause.message='{}'",
        httpStatus,
        rootCause.message)

    // When
    logger.debug("Creating ApplicationException with null error text and cause")
    val exception = ApplicationException(httpStatus, status, error, message, rootCause)

    // Then
    logger.debug("Verifying native error text was set from root cause")
    assertNotNull(exception)
    assertEquals("Root cause message", exception.error.nativeErrorText)
    logger.info(
        "✓ Native error text correctly set from root cause: '{}'", exception.error.nativeErrorText)
  }

  @Test
  fun constructorShouldSetDefaultErrorTextWhenRootCauseMessageIsNull() {
    logger.info("Testing default error text setting when root cause message is null")
    // Given
    val httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    val status = Status.ERROR
    val error = TestError(null)
    val message = "Test message"
    val cause = RuntimeException(null as String?) // null message
    logger.debug(
        "Test inputs: httpStatus={}, error.nativeErrorText=null, cause.message=null", httpStatus)

    // When
    logger.debug("Creating ApplicationException with null error text and null cause message")
    val exception = ApplicationException(httpStatus, status, error, message, cause)

    // Then
    logger.debug("Verifying default error text was set")
    assertNotNull(exception)
    assertEquals("Unknown root cause error", exception.error.nativeErrorText)
    logger.info("✓ Default error text correctly set: '{}'", exception.error.nativeErrorText)
  }

  @Test
  fun constructorShouldNotModifyNativeErrorTextWhenItAlreadyHasValue() {
    logger.info("Testing that existing native error text is preserved")
    // Given
    val httpStatus = HttpStatus.BAD_REQUEST
    val status = Status.ERROR
    val originalErrorText = "Original error text"
    val error = TestError(originalErrorText)
    val message = "Test message"
    val cause = RuntimeException("This should not override")
    logger.debug(
        "Test inputs: httpStatus={}, originalErrorText='{}', cause.message='{}'",
        httpStatus,
        originalErrorText,
        cause.message)

    // When
    logger.debug("Creating ApplicationException with existing error text and cause")
    val exception = ApplicationException(httpStatus, status, error, message, cause)

    // Then
    logger.debug("Verifying original error text was preserved")
    assertEquals(originalErrorText, exception.error.nativeErrorText)
    logger.info("✓ Original error text preserved: '{}'", exception.error.nativeErrorText)
  }

  @Test
  fun constructorShouldNotModifyErrorWhenCauseIsNull() {
    logger.info("Testing that error is not modified when cause is null")
    // Given
    val httpStatus = HttpStatus.BAD_REQUEST
    val status = Status.ERROR
    val error = TestError(null)
    val message = "Test message"
    logger.debug("Test inputs: httpStatus={}, error.nativeErrorText=null, cause=null", httpStatus)

    // When
    logger.debug("Creating ApplicationException with null error text and no cause")
    val exception = ApplicationException(httpStatus, status, error, message, null)

    // Then
    logger.debug("Verifying error text remains null when no cause is provided")
    assertNotNull(exception)
    assertNull(exception.error.nativeErrorText)
    logger.info("✓ Error text correctly left unmodified when cause is null")
  }

  @Test
  fun constructorShouldHandleChainOfCausesToFindRootCause() {
    logger.info("Testing exception cause chain traversal to find root cause")
    // Given
    val httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    val status = Status.ERROR
    val error = TestError("") // blank nativeErrorText
    val message = "Test message"

    // Create a chain of causes
    val rootCause = RuntimeException("Root cause message")
    val middleCause = IllegalStateException("Middle cause", rootCause)
    val immediateCause = IllegalArgumentException("Immediate cause", middleCause)
    logger.debug(
        "Created exception chain: {} -> {} -> {}",
        immediateCause.javaClass.simpleName,
        middleCause.javaClass.simpleName,
        rootCause.javaClass.simpleName)
    logger.debug("Root cause message: '{}'", rootCause.message)

    // When
    logger.debug("Creating ApplicationException with exception chain")
    val exception = ApplicationException(httpStatus, status, error, message, immediateCause)

    // Then
    logger.debug("Verifying root cause message was extracted from chain")
    assertNotNull(exception)
    assertEquals("Root cause message", exception.error.nativeErrorText)
    assertEquals(immediateCause, exception.cause)
    logger.info(
        "✓ Root cause message correctly extracted from exception chain: '{}'",
        exception.error.nativeErrorText)
  }

  @Test
  fun exceptionShouldExtendExceptionCorrectly() {
    logger.info("Testing ApplicationException inheritance from Exception class")
    // Given
    val httpStatus = HttpStatus.BAD_REQUEST
    val status = Status.ERROR
    val error = TestError()
    val message = "Test message"
    val cause = RuntimeException("Cause")
    logger.debug(
        "Test inputs: httpStatus={}, message='{}', cause='{}'", httpStatus, message, cause.message)

    // When
    logger.debug("Creating ApplicationException instance")
    val exception = ApplicationException(httpStatus, status, error, message, cause)

    // Then
    logger.debug("Verifying exception inheritance and properties")
    assertNotNull(exception as? Exception)
    assertEquals(message, exception.message)
    assertEquals(cause, exception.cause)
    logger.info(
        "✓ ApplicationException correctly extends Exception with message: '{}' and cause: '{}'",
        exception.message,
        exception.cause?.message)
  }

  @Test
  fun constructorShouldHandleExceptionCreationWithoutCause() {
    logger.info("Testing ApplicationException creation without cause")
    // Given
    val httpStatus = HttpStatus.BAD_REQUEST
    val status = Status.ERROR
    val error = TestError()
    val message = "Test message"
    logger.debug(
        "Test inputs: httpStatus={}, status={}, message='{}', cause=null",
        httpStatus,
        status,
        message)

    // When
    logger.debug("Creating ApplicationException without cause")
    val exception = ApplicationException(httpStatus, status, error, message, null)

    // Then
    logger.debug("Verifying exception created successfully without cause")
    assertNotNull(exception)
    assertEquals(error, exception.error)
    assertNull(exception.cause)
    logger.info(
        "✓ ApplicationException created successfully without cause, message: '{}'",
        exception.message)
  }
}
