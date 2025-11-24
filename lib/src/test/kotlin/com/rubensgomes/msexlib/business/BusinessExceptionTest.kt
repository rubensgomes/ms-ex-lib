package com.rubensgomes.msexlib.business

import com.rubensgomes.msbaselib.Status
import com.rubensgomes.msbaselib.error.ApplicationError
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class BusinessExceptionTest {
    @Test
    fun `should create BusinessException with valid parameters`() {
        val error = ApplicationError("BUS_ERR", "Business error")
        val exception = BusinessException(
            httpStatus = HttpStatus.CONFLICT,
            status = Status.FAILURE,
            error = error,
            message = "Business rule violated"
        )
        assertEquals(HttpStatus.CONFLICT, exception.httpStatus)
        assertEquals(Status.FAILURE, exception.status)
        assertEquals(error, exception.error)
        assertEquals("Business rule violated", exception.message)
    }
}

