package com.brzezinski.roche.rest

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import javax.validation.ConstraintViolationException

@ControllerAdvice
class CommonControllerAdvice {

    private val log = KotlinLogging.logger {}

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun constraintViolationException(ex: ConstraintViolationException): ResponseEntity<ExceptionResponse> {
        log.error("Got ConstraintValidationException", ex)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExceptionResponse(ex.constraintViolations.joinToString(",") { it.message }))
    }
}
