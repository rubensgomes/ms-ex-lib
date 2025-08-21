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
package com.rubensgomes.msexlib.security

import com.rubensgomes.msexlib.ApplicationException
import com.rubensgomes.msreqresplib.dto.Error
import com.rubensgomes.msreqresplib.dto.Status
import org.springframework.http.HttpStatus

/**
 * Security-related exception for handling authentication, authorization, and access control errors.
 *
 * This exception class extends [ApplicationException] to provide specialized handling for
 * security-related errors including authentication failures, authorization violations, access
 * control breaches, and security policy violations. It maintains all the validation and error
 * handling capabilities of the base class while providing security-specific context.
 *
 * Common use cases include:
 * - Authentication failures (invalid credentials, expired tokens)
 * - Authorization violations (insufficient permissions, access denied)
 * - Security policy violations (password complexity, account lockout)
 * - Access control breaches (unauthorized resource access)
 *
 * @param httpStatus The HTTP status code associated with this security exception. Must be an error
 *   status (4xx or 5xx). Common security-related status codes include 401 (Unauthorized), 403
 *   (Forbidden), and 429 (Too Many Requests).
 * @param status The status information providing additional context about the security error state.
 *   Cannot be Status.SUCCESS since this represents a security failure.
 * @param error The structured error details containing specific information about the security
 *   violation.
 * @param message A human-readable description of the security exception. This value cannot be
 *   blank.
 * @param cause The underlying cause of this security exception, or null if there is no underlying
 *   cause. Useful for chaining security-related exceptions and preserving error context.
 * @throws IllegalArgumentException if httpStatus is not an error status (not 4xx or 5xx)
 * @throws IllegalArgumentException if message is blank or empty
 * @throws IllegalArgumentException if status is Status.SUCCESS
 * @see ApplicationException
 * @see Status
 * @see Error
 * @author Rubens Gomes
 */
open class SecurityException(
    httpStatus: HttpStatus,
    status: Status,
    error: Error,
    message: String,
    cause: Throwable?,
) : ApplicationException(httpStatus, status, error, message, cause)
