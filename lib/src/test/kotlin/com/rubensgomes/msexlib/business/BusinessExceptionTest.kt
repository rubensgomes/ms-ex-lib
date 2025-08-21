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
package com.rubensgomes.msexlib.business

import com.rubensgomes.msexlib.ApplicationException
import com.rubensgomes.reqresp.dto.Error
import com.rubensgomes.reqresp.dto.ErrorCode
import com.rubensgomes.reqresp.dto.Status
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus

/**
 * Unit tests for [BusinessException] class.
 *
 * Tests cover inheritance from ApplicationException, business-specific error handling, and
 * validation logic specific to business domain errors.
 */
class BusinessExceptionTest {

  private val logger = LoggerFactory.getLogger(BusinessExceptionTest::class.java)

  // Mock implementation of Error interface for testing
  private class TestError(
      private var _nativeErrorText: String? = null,
      private val errorDescription: String = "Test business error description"
  ) : Error {

    override fun getErrorDescription(): String = errorDescription

    override fun getNativeErrorText(): String? = _nativeErrorText

    override fun setNativeErrorText(nativeErrorText: String?) {
      this._nativeErrorText = nativeErrorText
    }

    override fun getErrorCode(): ErrorCode {
      return object : ErrorCode {
        override fun getCode(): String = "BUSINESS_ERROR"

        override fun getDescription(): String = "Business error code"
      }
    }
  }

  @Test
  fun constructorShouldCreateBusinessExceptionWithValidParameters() {
    logger.info("Testing BusinessException constructor with valid parameters")
    // Given
    val httpStatus = HttpStatus.CONFLICT
    val status = Status.ERROR
    val error = TestError("Business rule violation")
    val message = "Insufficient funds for transaction"
    val cause = RuntimeException("Account balance check failed")
    logger.debug(
        "Test inputs: httpStatus={}, status={}, message='{}', cause='{}'",
        httpStatus,
        status,
        message,
        cause.message)

    // When
    logger.debug("Creating BusinessException instance")
    val exception = BusinessException(httpStatus, status, error, message, cause)

    // Then
    logger.debug("Verifying exception properties")
    assertEquals(httpStatus, exception.httpStatus)
    assertEquals(status, exception.status)
    assertEquals(error, exception.error)
    assertEquals(message, exception.message)
    assertEquals(cause, exception.cause)
    logger.info("✓ BusinessException created successfully with valid parameters")
  }

  @Test
  fun businessExceptionShouldExtendApplicationException() {
    logger.info("Testing BusinessException inheritance from ApplicationException")
    // Given
    val httpStatus = HttpStatus.BAD_REQUEST
    val status = Status.ERROR
    val error = TestError()
    val message = "Business validation failed"
    val cause = RuntimeException("Domain rule violation")
    logger.debug(
        "Test inputs: httpStatus={}, message='{}', cause='{}'", httpStatus, message, cause.message)

    // When
    logger.debug("Creating BusinessException instance")
    val exception = BusinessException(httpStatus, status, error, message, cause)

    // Then
    logger.debug("Verifying inheritance and type checking")
    assertTrue(exception is ApplicationException)
    assertTrue(exception is BusinessException)
    assertNotNull(exception as? Exception)
    logger.info("✓ BusinessException correctly extends ApplicationException")
  }

  @Test
  fun constructorShouldThrowWhenHttpStatusIsNotErrorStatus() {
    logger.info("Testing BusinessException constructor with non-error HTTP status")
    // Given
    val httpStatus = HttpStatus.OK // 200 - not an error status
    val status = Status.ERROR
    val error = TestError()
    val message = "Business operation failed"
    logger.debug(
        "Test inputs: httpStatus={} (non-error status), status={}, message='{}'",
        httpStatus,
        status,
        message)

    // When & Then
    logger.debug("Expecting IllegalArgumentException to be thrown")
    val exception =
        assertThrows<IllegalArgumentException> {
          BusinessException(httpStatus, status, error, message, null)
        }
    assertEquals("HTTP status must be an error status, got: $httpStatus", exception.message)
    logger.info(
        "✓ IllegalArgumentException correctly thrown for non-error HTTP status: {}", httpStatus)
  }

  @Test
  fun constructorShouldThrowWhenStatusIsSuccess() {
    logger.info("Testing BusinessException constructor with SUCCESS status")
    // Given
    val httpStatus = HttpStatus.CONFLICT
    val status = Status.SUCCESS // Not allowed for exceptions
    val error = TestError()
    val message = "Business rule violation"
    logger.debug(
        "Test inputs: httpStatus={}, status={} (not allowed for exceptions), message='{}'",
        httpStatus,
        status,
        message)

    // When & Then
    logger.debug("Expecting IllegalArgumentException to be thrown for SUCCESS status")
    val exception =
        assertThrows<IllegalArgumentException> {
          BusinessException(httpStatus, status, error, message, null)
        }
    assertEquals("Exception status cannot be SUCCESS, got: $status", exception.message)
    logger.info("✓ IllegalArgumentException correctly thrown for SUCCESS status")
  }

  @Test
  fun constructorShouldWorkWithCommonBusinessHttpStatuses() {
    logger.info("Testing BusinessException constructor with common business HTTP statuses")
    val businessHttpStatuses =
        listOf(
            HttpStatus.BAD_REQUEST, // 400 - Invalid business data
            HttpStatus.CONFLICT, // 409 - Business rule conflict
            HttpStatus.UNPROCESSABLE_ENTITY, // 422 - Business validation failure
            HttpStatus.PRECONDITION_FAILED, // 412 - Business precondition not met
            HttpStatus.FORBIDDEN // 403 - Business authorization failure
            )
    logger.debug(
        "Testing with {} different business-related HTTP statuses", businessHttpStatuses.size)

    businessHttpStatuses.forEach { httpStatus ->
      // Given
      val status = Status.ERROR
      val error = TestError()
      val message = "Business error for $httpStatus"
      logger.trace("Testing with httpStatus: {}", httpStatus)

      // When & Then (should not throw)
      val exception = BusinessException(httpStatus, status, error, message, null)
      assertEquals(httpStatus, exception.httpStatus)
      logger.trace("✓ Successfully created BusinessException with httpStatus: {}", httpStatus)
    }
    logger.info(
        "✓ All {} business-related HTTP statuses handled correctly", businessHttpStatuses.size)
  }

  @Test
  fun constructorShouldSetNativeErrorTextFromBusinessCause() {
    logger.info("Testing native error text setting from business-related cause")
    // Given
    val httpStatus = HttpStatus.CONFLICT
    val status = Status.ERROR
    val error = TestError("") // blank nativeErrorText
    val message = "Order processing failed"
    val businessCause = RuntimeException("Inventory insufficient for requested quantity")
    logger.debug(
        "Test inputs: httpStatus={}, error.nativeErrorText='{}' (blank), businessCause.message='{}'",
        httpStatus,
        error.getNativeErrorText(),
        businessCause.message)

    // When
    logger.debug("Creating BusinessException with blank error text and business cause")
    val exception = BusinessException(httpStatus, status, error, message, businessCause)

    // Then
    logger.debug("Verifying native error text was set from business cause")
    assertNotNull(exception)
    assertEquals("Inventory insufficient for requested quantity", exception.error.nativeErrorText)
    logger.info(
        "✓ Native error text correctly set from business cause: '{}'",
        exception.error.nativeErrorText)
  }

  @Test
  fun constructorShouldPreserveExistingBusinessErrorText() {
    logger.info("Testing that existing business error text is preserved")
    // Given
    val httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
    val status = Status.ERROR
    val originalBusinessError = "Customer credit limit exceeded"
    val error = TestError(originalBusinessError)
    val message = "Payment processing failed"
    val cause = RuntimeException("This should not override business error")
    logger.debug(
        "Test inputs: httpStatus={}, originalBusinessError='{}', cause.message='{}'",
        httpStatus,
        originalBusinessError,
        cause.message)

    // When
    logger.debug("Creating BusinessException with existing business error text")
    val exception = BusinessException(httpStatus, status, error, message, cause)

    // Then
    logger.debug("Verifying original business error text was preserved")
    assertEquals(originalBusinessError, exception.error.nativeErrorText)
    logger.info("✓ Original business error text preserved: '{}'", exception.error.nativeErrorText)
  }

  @Test
  fun constructorShouldHandleChainedBusinessExceptions() {
    logger.info("Testing BusinessException with chained business-related causes")
    // Given
    val httpStatus = HttpStatus.CONFLICT
    val status = Status.ERROR
    val error = TestError("") // blank nativeErrorText
    val message = "Transaction failed"

    // Create a chain of business-related causes
    val rootCause = RuntimeException("Database constraint violation")
    val businessCause = IllegalStateException("Account is frozen", rootCause)
    val validationCause = IllegalArgumentException("Invalid transaction amount", businessCause)
    logger.debug(
        "Created business exception chain: {} -> {} -> {}",
        validationCause.javaClass.simpleName,
        businessCause.javaClass.simpleName,
        rootCause.javaClass.simpleName)
    logger.debug("Root cause message: '{}'", rootCause.message)

    // When
    logger.debug("Creating BusinessException with business exception chain")
    val exception = BusinessException(httpStatus, status, error, message, validationCause)

    // Then
    logger.debug("Verifying root cause message was extracted from business chain")
    assertNotNull(exception)
    assertEquals("Database constraint violation", exception.error.nativeErrorText)
    assertEquals(validationCause, exception.cause)
    logger.info(
        "✓ Root cause message correctly extracted from business exception chain: '{}'",
        exception.error.nativeErrorText)
  }

  @Test
  fun constructorShouldHandleBusinessExceptionWithoutCause() {
    logger.info("Testing BusinessException creation without cause")
    // Given
    val httpStatus = HttpStatus.BAD_REQUEST
    val status = Status.ERROR
    val error = TestError()
    val message = "Business validation error"
    logger.debug(
        "Test inputs: httpStatus={}, status={}, message='{}', cause=null",
        httpStatus,
        status,
        message)

    // When
    logger.debug("Creating BusinessException without cause")
    val exception = BusinessException(httpStatus, status, error, message, null)

    // Then
    logger.debug("Verifying business exception created successfully without cause")
    assertNotNull(exception)
    assertEquals(error, exception.error)
    assertNull(exception.cause)
    assertEquals(message, exception.message)
    logger.info(
        "✓ BusinessException created successfully without cause, message: '{}'", exception.message)
  }

  @Test
  fun businessExceptionShouldMaintainBusinessContext() {
    logger.info("Testing BusinessException maintains business context and properties")
    // Given
    val httpStatus = HttpStatus.PRECONDITION_FAILED
    val status = Status.ERROR
    val error = TestError("Business rule validation failed")
    val businessMessage = "Customer does not meet eligibility criteria for this product"
    val businessCause = RuntimeException("Age verification failed")
    logger.debug(
        "Business context: httpStatus={}, message='{}', cause='{}'",
        httpStatus,
        businessMessage,
        businessCause.message)

    // When
    logger.debug("Creating BusinessException with business context")
    val exception = BusinessException(httpStatus, status, error, businessMessage, businessCause)

    // Then
    logger.debug("Verifying business context is maintained")
    assertEquals(httpStatus, exception.httpStatus)
    assertEquals(status, exception.status)
    assertEquals(error, exception.error)
    assertEquals(businessMessage, exception.message)
    assertEquals(businessCause, exception.cause)

    // Verify it's still a business exception type
    assertTrue(exception is BusinessException)
    logger.info(
        "✓ BusinessException maintains all business context: status={}, message='{}'",
        exception.httpStatus,
        exception.message)
  }
}
