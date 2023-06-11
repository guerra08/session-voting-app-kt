package com.example.sessionvotingappkt.web.rest

import com.example.sessionvotingappkt.service.ScheduleService
import com.example.sessionvotingappkt.service.SessionService
import com.example.sessionvotingappkt.web.dto.request.CreateScheduleRequest
import com.example.sessionvotingappkt.web.dto.request.CreateSessionRequest
import com.example.sessionvotingappkt.web.dto.response.ScheduleResponse
import com.example.sessionvotingappkt.web.dto.response.SessionResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/schedules")
@Tag(name = "schedules", description = "Schedules REST API")
class ScheduleController(
    private val scheduleService: ScheduleService,
    private val sessionService: SessionService
) {

    @GetMapping
    @Operation(summary = "Gets Schedules with pagination support")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "successful operation")
        ]
    )
    fun getSchedules(@ParameterObject pageable: Pageable): ResponseEntity<Page<ScheduleResponse>> {
        val result = scheduleService.findAll(pageable)
        return ResponseEntity.ok(result)
    }

    @PostMapping
    @Operation(summary = "Registers a new Schedule")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "successful creation"),
            ApiResponse(responseCode = "400", description = "bad request")
        ]
    )
    fun postSchedule(@Valid @RequestBody createScheduleRequest: CreateScheduleRequest): ResponseEntity<ScheduleResponse> {
        val result = scheduleService.create(createScheduleRequest)
        return ResponseEntity.created(URI.create("/schedules/${result.id}")).body(result)
    }

    @PostMapping("/{id}/session")
    @Operation(summary = "Registers and opens a new Session under a Schedule")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "successful creation"),
            ApiResponse(responseCode = "400", description = "bad request"),
            ApiResponse(responseCode = "404", description = "schedule not found", useReturnTypeSchema = false),
        ]
    )
    fun postSession(
        @PathVariable id: Long,
        @RequestBody createSessionRequest: CreateSessionRequest
    ): ResponseEntity<SessionResponse> {
        if (!scheduleService.existsById(id)) {
            return ResponseEntity.notFound().build()
        }
        val result = sessionService.open(id, createSessionRequest)
        return ResponseEntity.created(URI.create("/schedules/${result.id}")).body(result)
    }

    @GetMapping("/{id}/session")
    @Operation(summary = "Gets the current active Session of a Schedule")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "successful operation"),
            ApiResponse(responseCode = "204", description = "no content (no active session found for schedule)"),
            ApiResponse(responseCode = "404", description = "schedule not found", useReturnTypeSchema = false),
        ]
    )
    fun getSessionOfSchedule(@PathVariable id: Long): ResponseEntity<SessionResponse> {
        if (!scheduleService.existsById(id)) {
            return ResponseEntity.notFound().build()
        }
        val result = sessionService.findCurrentOfSchedule(id)
        return result?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.noContent().build()
    }

}
