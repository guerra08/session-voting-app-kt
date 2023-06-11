package com.example.sessionvotingappkt.handler

import com.example.sessionvotingappkt.exception.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @ExceptionHandler(BadVoteRequestException::class)
    fun handleBadVoteRequestException(e: BadVoteRequestException): ProblemDetail {
        log.info("Bad vote request", e)
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message)
    }

    @ExceptionHandler(ScheduleNotFoundException::class)
    fun handleScheduleNotFoundException(e: ScheduleNotFoundException): ProblemDetail {
        return ProblemDetail.forStatus(HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(SessionConflictException::class)
    fun handleScheduleNotFoundException(e: SessionConflictException): ProblemDetail {
        log.info("Session conflict", e)
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message)
    }

    @ExceptionHandler(VoteConflictException::class)
    fun handleVoteConflictException(e: VoteConflictException): ProblemDetail {
        log.info("Vote conflict", e)
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message)
    }

    @ExceptionHandler(SessionNotFoundException::class)
    fun handleSessionNotFoundException(e: SessionNotFoundException): ProblemDetail {
        return ProblemDetail.forStatus(HttpStatus.NOT_FOUND)
    }

}