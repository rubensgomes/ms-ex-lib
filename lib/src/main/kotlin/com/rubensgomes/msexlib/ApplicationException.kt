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
package com.rubensgomes.msexlib

import com.rubensgomes.msbaselib.Status
import com.rubensgomes.msbaselib.error.ApplicationError
import org.springframework.http.HttpStatus

/**
 * Base application exception class for handling HTTP-related errors with structured error
 * information.
 *
 * This exception class provides a standardized way to represent errors that occur within the
 * application, including HTTP status codes, structured error details, and status information. It
 * extends the standard [Exception] class while adding validation constraints to ensure proper error
 * handling.
 *
 * @param httpStatus The HTTP status code associated with this exception. Must be between 400 and
 *   599 (inclusive) to represent client and server error status codes according to HTTP
 *   specifications.
 * @param status The status information providing additional context about the error state. Cannot
 *   be Status.SUCCESS since this is an exception class for error conditions only.
 * @param error The structured error details containing specific information about what went wrong.
 * @param message A human-readable description of the exception. This value cannot be blank and will
 *   be used as the exception message.
 * @param cause The underlying cause of this exception, or null if there is no underlying cause.
 *   This can be used to chain exceptions and preserve the original error context.
 * @throws IllegalArgumentException if httpStatus is not within the valid range (400-599)
 * @throws IllegalArgumentException if message is blank or empty
 * @throws IllegalArgumentException if status is Status.SUCCESS
 * @see Exception
 * @see Status
 * @see ApplicationError
 * @author Rubens Gomes
 */
open class ApplicationException(
    val httpStatus: HttpStatus,
    val status: Status,
    val error: ApplicationError,
    override val message: String,
    cause: Throwable?,
) : Exception(message, cause) {

  init {
    require(httpStatus.isError) { "HTTP status must be an error status, got: $httpStatus" }
    require(message.isNotBlank()) { "Exception message must not be blank" }
    require(status != Status.SUCCESS) { "Exception status cannot be SUCCESS, got: $status" }
  }
}
