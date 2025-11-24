package com.rubensgomes.msexlib.security

import com.rubensgomes.msbaselib.Status
import com.rubensgomes.msbaselib.error.ApplicationError
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class SecurityExceptionTest {
    @Test
    fun `should create SecurityException with valid parameters`() {
        val error = ApplicationError("SEC_ERR", "Security error")
        val exception = SecurityException(
            httpStatus = HttpStatus.UNAUTHORIZED,
            status = Status.FAILURE,
            error = error,
            message = "Unauthorized access"
        )
        assertEquals(HttpStatus.UNAUTHORIZED, exception.httpStatus)
        assertEquals(Status.FAILURE, exception.status)
        assertEquals(error, exception.error)
        assertEquals("Unauthorized access", exception.message)
    }
}

