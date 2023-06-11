package com.example.sessionvotingappkt.web.rest

import com.example.sessionvotingappkt.service.VoteService
import com.example.sessionvotingappkt.web.dto.request.CreateVoteRequest
import com.example.sessionvotingappkt.web.dto.response.ScheduleReportResponse
import com.example.sessionvotingappkt.web.dto.response.SessionReportResponse
import com.example.sessionvotingappkt.web.dto.response.VoteResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/votes")
@Tag(name = "votes", description = "Votes REST API")
class VoteController(
    val voteService: VoteService
) {

    @PostMapping("/schedule/{id}")
    @Operation(summary = "Submits a vote for the active Session of a Schedule")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "successful creation"),
            ApiResponse(responseCode = "400", description = "bad request"),
            ApiResponse(responseCode = "404", description = "schedule not found", useReturnTypeSchema = false),
            ApiResponse(responseCode = "409", description = "conflict"),
        ]
    )
    fun postVoteForSchedule(
        @PathVariable id: Long,
        @Valid @RequestBody createVoteRequest: CreateVoteRequest
    ): ResponseEntity<VoteResponse> {
        val result = voteService.voteForSessionOfSchedule(id, createVoteRequest)
        return ResponseEntity.created(URI.create("")).body(result)
    }

    @GetMapping("/result/session/{id}")
    @Operation(summary = "Gets the result report of voting for a given Session")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "successful operation"),
            ApiResponse(responseCode = "404", description = "session not found", useReturnTypeSchema = false)
        ]
    )
    fun getVotingReportOfSession(@PathVariable id: Long): ResponseEntity<SessionReportResponse> {
        val result = voteService.getReportForSession(id)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/result/schedule/{id}")
    @Operation(summary = "Gets the result report of voting for a given Schedule (can contain multiple Sessions)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "successful operation"),
            ApiResponse(responseCode = "404", description = "schedule not found")
        ]
    )
    fun getVotingReportOfSchedule(@PathVariable id: Long): ResponseEntity<ScheduleReportResponse> {
        val result = voteService.getReportForSchedule(id)
        return ResponseEntity.ok(result)
    }

}