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
package com.rubensgomes.msexlib.security

import com.rubensgomes.msbaselib.Status
import com.rubensgomes.msbaselib.error.ApplicationError
import com.rubensgomes.msbaselib.error.ApplicationErrorCode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class SecurityExceptionTest {

  private fun createTestError(code: String, description: String): ApplicationError =
      object : ApplicationError {
        private var nativeErrorText: String? = null

        override fun getErrorDescription(): String = description

        override fun getNativeErrorText(): String? = nativeErrorText

        override fun setNativeErrorText(text: String?) {
          nativeErrorText = text
        }

        override fun getErrorCode(): ApplicationErrorCode =
            object : ApplicationErrorCode {
              override fun getCode(): String = code

              override fun getDescription(): String = description
            }
      }

  @Test
  fun `should create SecurityException with valid parameters`() {
    val error = createTestError("SEC_ERR", "Security error")
    val exception =
        SecurityException(
            httpStatus = HttpStatus.UNAUTHORIZED,
            status = Status.FAILURE,
            error = error,
            message = "Unauthorized access",
            cause = null,
        )
    assertEquals(HttpStatus.UNAUTHORIZED, exception.httpStatus)
    assertEquals(Status.FAILURE, exception.status)
    assertEquals(error, exception.error)
    assertEquals("Unauthorized access", exception.message)
  }
}
