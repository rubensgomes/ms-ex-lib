package com.rubensgomes.ex

import com.rubensgomes.reqresp.BaseResponse
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class ApplicationExceptionTest {
    private lateinit var validator: Validator

    @BeforeTest
    fun setUp() {
        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `ensure constructor 1 works`() {
        val ex = ApplicationException(400, BaseResponse.Status.FAILED, "error")
        assertNotNull(ex)
    }

    @Test
    fun `ensure constructor 2 works`() {
        val ex =
            ApplicationException(
                400,
                BaseResponse.Status.PARTIAL,
                "error",
                "native",
            )
        assertNotNull(ex)
    }

    @Test
    fun `ensure constructor 3 works`() {
        val exception = RuntimeException()
        val ex =
            ApplicationException(
                400,
                BaseResponse.Status.UNPROCESSED,
                "error",
                "native",
                exception,
            )
        assertNotNull(ex)
    }

    @Test
    fun `ensure constructor 4 works`() {
        val exception = RuntimeException()
        val ex =
            ApplicationException(
                400,
                BaseResponse.Status.UNPROCESSED,
                "error",
                "native",
                exception,
            )
        assertEquals(BaseResponse.Status.UNPROCESSED, ex.status)
    }

    @Test
    fun `fail due to invalid http status 399`() {
        val ex = ApplicationException(399, BaseResponse.Status.FAILED, "error")
        val constraintViolations: Set<ConstraintViolation<ApplicationException>> =
            validator.validate(ex)
        assertEquals(1, constraintViolations.size)
    }

    @Test
    fun `fail due to invalid http status 600`() {
        val ex = ApplicationException(600, BaseResponse.Status.FAILED, "error")
        val constraintViolations: Set<ConstraintViolation<ApplicationException>> =
            validator.validate(ex)
        assertEquals(1, constraintViolations.size)
    }

    @Test
    fun `fail due to blank exception message`() {
        val ex = ApplicationException(500, BaseResponse.Status.UNPROCESSED, " ")
        val constraintViolations: Set<ConstraintViolation<ApplicationException>> =
            validator.validate(ex)
        assertEquals(1, constraintViolations.size)
    }

    @Test
    fun `fail due to invalid status message`() {
        assertFailsWith<IllegalArgumentException>(
            message = "illegal constructor argument",
            block = { ApplicationException(500, BaseResponse.Status.SUCCESS, "error") },
        )
    }

    @Test
    fun `ensure constructor works for valid http status 400`() {
        val ex =
            ApplicationException(
                400,
                BaseResponse.Status.UNPROCESSED,
                "client error",
            )
        val constraintViolations: Set<ConstraintViolation<ApplicationException>> =
            validator.validate(ex)
        assertEquals(0, constraintViolations.size)
    }

    @Test
    fun `ensure constructor works for valid http status 599`() {
        val ex =
            ApplicationException(
                599,
                BaseResponse.Status.PARTIAL,
                "server error",
            )
        val constraintViolations: Set<ConstraintViolation<ApplicationException>> =
            validator.validate(ex)
        assertEquals(0, constraintViolations.size)
    }
}
