package com.rubensgomes.msexlib.system

import com.rubensgomes.msbaselib.Status
import com.rubensgomes.msbaselib.error.impl.ApplicationErrorCodeImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class SystemExceptionTest {
    @Test
    fun `should create SystemException with valid parameters`() {
        val error = ApplicationErrorCodeImpl("SYS_ERR", "System error")
        val exception = SystemException(
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
            status = Status.FAILURE,
            error = error,
            message = "System failure"
        )
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.httpStatus)
        assertEquals(Status.FAILURE, exception.status)
        assertEquals(error, exception.error)
        assertEquals("System failure", exception.message)
    }
}
package com.rubensgomes.msexlib

import com.rubensgomes.msbaselib.Status
import com.rubensgomes.msbaselib.error.ApplicationError
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ApplicationExceptionTest {
    @Test
    fun `should create ApplicationException with valid parameters`() {
        val error = ApplicationError("ERR_CODE", "Error message")
        val exception = ApplicationException(
            httpStatus = HttpStatus.BAD_REQUEST,
            status = Status.FAILURE,
            error = error,
            message = "Test exception"
        )
        assertEquals(HttpStatus.BAD_REQUEST, exception.httpStatus)
        assertEquals(Status.FAILURE, exception.status)
        assertEquals(error, exception.error)
        assertEquals("Test exception", exception.message)
    }

    @Test
    fun `should throw IllegalArgumentException for invalid httpStatus`() {
        val error = ApplicationError("ERR_CODE", "Error message")
        assertThrows(IllegalArgumentException::class.java) {
            ApplicationException(
                httpStatus = HttpStatus.OK,
                status = Status.FAILURE,
                error = error,
                message = "Invalid status"
            )
        }
    }

    @Test
    fun `should throw IllegalArgumentException for blank message`() {
        val error = ApplicationError("ERR_CODE", "Error message")
        assertThrows(IllegalArgumentException::class.java) {
            ApplicationException(
                httpStatus = HttpStatus.BAD_REQUEST,
                status = Status.FAILURE,
                error = error,
                message = ""
            )
        }
    }

    @Test
    fun `should throw IllegalArgumentException for Status SUCCESS`() {
        val error = ApplicationError("ERR_CODE", "Error message")
        assertThrows(IllegalArgumentException::class.java) {
            ApplicationException(
                httpStatus = HttpStatus.BAD_REQUEST,
                status = Status.SUCCESS,
                error = error,
                message = "Status success"
            )
        }
    }
}

