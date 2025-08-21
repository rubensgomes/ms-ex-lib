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
import com.rubensgomes.msreqresplib.dto.Error
import com.rubensgomes.msreqresplib.dto.Status
import org.springframework.http.HttpStatus

/**
 * System-level exception for handling infrastructure failures, resource management issues, and
 * technical errors.
 *
 * This exception class extends [ApplicationException] to provide specialized handling for
 * system-level errors including infrastructure failures, resource management problems,
 * configuration issues, and low-level technical errors. It maintains all the validation and error
 * handling capabilities of the base class while providing system-specific context.
 *
 * Common use cases include:
 * - Infrastructure failures (database connectivity, network issues, service unavailability)
 * - Resource management problems (memory exhaustion, file system errors, connection pool
 *   exhaustion)
 * - Configuration errors (missing properties, invalid configuration values)
 * - External service failures (third-party API failures, timeout errors)
 * - System capacity issues (rate limiting, quota exceeded, resource constraints)
 *
 * @param httpStatus The HTTP status code associated with this system exception. Must be an error
 *   status (4xx or 5xx). Common system-related status codes include 500 (Internal Server Error),
 *   502 (Bad Gateway), 503 (Service Unavailable), and 504 (Gateway Timeout).
 * @param status The status information providing additional context about the system error state.
 *   Cannot be Status.SUCCESS since this represents a system failure.
 * @param error The structured error details containing specific information about the system
 *   failure.
 * @param message A human-readable description of the system exception. This value cannot be blank.
 * @param cause The underlying cause of this system exception, or null if there is no underlying
 *   cause. Useful for chaining system-related exceptions and preserving technical error context.
 * @throws IllegalArgumentException if httpStatus is not an error status (not 4xx or 5xx)
 * @throws IllegalArgumentException if message is blank or empty
 * @throws IllegalArgumentException if status is Status.SUCCESS
 * @see ApplicationException
 * @see Status
 * @see Error
 * @author Rubens Gomes
 */
open class SystemException(
    httpStatus: HttpStatus,
    status: Status,
    error: Error,
    message: String,
    cause: Throwable?,
) : ApplicationException(httpStatus, status, error, message, cause)
