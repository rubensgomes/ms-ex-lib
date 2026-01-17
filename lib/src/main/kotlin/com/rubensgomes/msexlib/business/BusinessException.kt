/*
 * Copyright 2025 Rubens Gomes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
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

import com.rubensgomes.msbaselib.Status
import com.rubensgomes.msbaselib.error.ApplicationError
import com.rubensgomes.msexlib.ApplicationException
import org.springframework.http.HttpStatus

/**
 * Business logic exception for handling domain-specific errors, business rule violations, and
 * validation failures.
 *
 * This exception class extends [ApplicationException] to provide specialized handling for
 * business-related errors including domain rule violations, business validation failures, workflow
 * errors, and business process exceptions. It maintains all the validation and error handling
 * capabilities of the base class while providing business-specific context.
 *
 * Common use cases include:
 * - Business rule violations (insufficient funds, invalid state transitions, policy violations)
 * - Domain validation failures (invalid business data, constraint violations, referential
 *   integrity)
 * - Workflow errors (process step failures, approval violations, sequence errors)
 * - Business process exceptions (order processing failures, payment errors, inventory issues)
 * - Data integrity violations (duplicate records, missing required relationships)
 *
 * @param httpStatus The HTTP status code associated with this business exception. Must be an error
 *   status (4xx or 5xx). Common business-related status codes include 400 (Bad Request), 409
 *   (Conflict), 422 (Unprocessable Entity), and 412 (Precondition Failed).
 * @param status The status information providing additional context about the business error state.
 *   Cannot be Status.SUCCESS since this represents a business failure.
 * @param error The structured error details containing specific information about the business
 *   violation.
 * @param message A human-readable description of the business exception. This value cannot be
 *   blank.
 * @param cause The underlying cause of this business exception, or null if there is no underlying
 *   cause. Useful for chaining business-related exceptions and preserving domain error context.
 * @throws IllegalArgumentException if httpStatus is not an error status (not 4xx or 5xx)
 * @throws IllegalArgumentException if message is blank or empty
 * @throws IllegalArgumentException if status is Status.SUCCESS
 * @see ApplicationException
 * @see Status
 * @see Error
 * @author Rubens Gomes
 */
open class BusinessException(
    httpStatus: HttpStatus,
    status: Status,
    error: ApplicationError,
    message: String,
    cause: Throwable?,
) : ApplicationException(httpStatus, status, error, message, cause)
