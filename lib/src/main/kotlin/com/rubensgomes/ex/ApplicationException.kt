package com.rubensgomes.ex

import com.rubensgomes.reqresp.BaseResponse.Status
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * A base aaplication exception type that contains extra attributes, such as an error HTTP status
 * code, [Status] code to faciliate generating complete error HTTP responses; that is, using
 * appropriate HTTP status code and [Status].
 *
 * @author Rubens Gomes
 */
open class ApplicationException(
    @field:Min(
        400,
        message = "The value of an error HttpStatus must be greater than 400",
    )
    @field:Max(
        599,
        message = "The value of an error HttpStatus must be less than 599",
    )
    val httpStatus: Int,
    val status: Status,
    @field:NotBlank(message = "The value of exception message must not be blank")
    override val message: String,
    private val nativeErrorMessage: String?,
    cause: Throwable?,
) : Exception(message, cause) {
    init {
        if (status == Status.SUCCESS) {
            log.error("{} is not valid for an exception", status.name)
            throw IllegalArgumentException("status must NOT be SUCCESS")
        }
    }

    constructor(
        httpStatus: Int,
        status: Status,
        message: String,
    ) : this(httpStatus, status, message, null, null)

    constructor(
        httpStatus: Int,
        status: Status,
        message: String,
        nativeMessage: String?,
    ) : this(httpStatus, status, message, nativeMessage, null)

    override fun toString(): String =
        "ApplicationException(" +
            "httpStatus=$httpStatus, " +
            "status=$status, " +
            "message='$message', " +
            "nativeErrorMessage=$nativeErrorMessage" +
            ")"

    internal companion object {
        private val log: Logger = LoggerFactory.getLogger(ApplicationException::class.java)
    }
}
