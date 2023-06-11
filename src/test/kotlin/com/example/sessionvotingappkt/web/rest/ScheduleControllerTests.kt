package com.example.sessionvotingappkt.web.rest

import com.example.sessionvotingappkt.exception.SessionConflictException
import com.example.sessionvotingappkt.service.ScheduleService
import com.example.sessionvotingappkt.service.SessionService
import com.example.sessionvotingappkt.web.dto.request.CreateScheduleRequest
import com.example.sessionvotingappkt.web.dto.request.CreateSessionRequest
import com.example.sessionvotingappkt.web.dto.response.ScheduleResponse
import com.example.sessionvotingappkt.web.dto.response.SessionResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import java.time.temporal.ChronoUnit

@WebMvcTest(controllers = [ScheduleController::class])
class ScheduleControllerTests(@Autowired val mockMvc: MockMvc) {

    @MockBean
    lateinit var scheduleService: ScheduleService

    @MockBean
    lateinit var sessionService: SessionService

    @Test
    fun `should return paginated response of all schedules`() {
        mockMvc.perform(get("/schedules"))
            .andExpect(status().isOk)
    }

    @Test
    fun `should return bad request when trying to create request with bad data`() {
        val request = CreateScheduleRequest(name = "")
        mockMvc.perform(
            post("/schedules")
                .contentType("application/json")
                .content(ObjectMapper().writeValueAsString(request))
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should register schedule`() {
        val request = CreateScheduleRequest(name = "Name of the Schedule")
        whenever(scheduleService.create(request))
            .thenReturn(ScheduleResponse(id = 1L, name = request.name))
        mockMvc.perform(
            post("/schedules")
                .contentType("application/json")
                .content(ObjectMapper().writeValueAsString(request))
        ).andExpect(status().isCreated)
    }

    @Test
    fun `should open session`() {
        val request = CreateSessionRequest(durationMinutes = 10)
        whenever(scheduleService.existsById(1L)).thenReturn(true)
        whenever(sessionService.open(1L, request))
            .thenReturn(SessionResponse(1L, 10, Instant.now(), Instant.now().plus(10, ChronoUnit.MINUTES)))
        mockMvc.perform(
            post("/schedules/1/session")
                .contentType("application/json")
                .content(ObjectMapper().writeValueAsString(request))
        ).andExpect(status().isCreated)
    }

    @Test
    fun `should return not found when trying to open session for non existing schedule`() {
        val request = CreateSessionRequest(durationMinutes = 10)
        whenever(scheduleService.existsById(1L)).thenReturn(false)
        mockMvc.perform(
            post("/schedules/1/session")
                .contentType("application/json")
                .content(ObjectMapper().writeValueAsString(request))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should return conflict when trying to open session when is already open`() {
        val request = CreateSessionRequest(durationMinutes = 10)
        whenever(scheduleService.existsById(1L)).thenReturn(true)
        whenever(
            sessionService.open(
                1L,
                request
            )
        ).thenThrow(SessionConflictException("An open Session already exists for this Schedule"))
        mockMvc.perform(
            post("/schedules/1/session")
                .contentType("application/json")
                .content(ObjectMapper().writeValueAsString(request))
        ).andExpect(status().isConflict)
    }

    @Test
    fun `should return current open session of schedule`() {
        whenever(scheduleService.existsById(1L)).thenReturn(true)
        whenever(sessionService.findCurrentOfSchedule(1L))
            .thenReturn(SessionResponse(1L, 10, Instant.now(), Instant.now().plus(10, ChronoUnit.MINUTES)))

        mockMvc.perform(
            get("/schedules/1/session")
                .contentType("application/json")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should return not found when trying to get current session of non existing schedule`() {
        whenever(scheduleService.existsById(1L)).thenReturn(false)
        whenever(sessionService.findCurrentOfSchedule(1L))
            .thenReturn(SessionResponse(1L, 10, Instant.now(), Instant.now().plus(10, ChronoUnit.MINUTES)))

        mockMvc.perform(
            get("/schedules/1/session")
                .contentType("application/json")
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should return no content when trying to get current session of schedule when none is open`() {
        whenever(scheduleService.existsById(1L)).thenReturn(true)
        whenever(sessionService.findCurrentOfSchedule(1L)).thenReturn(null)

        mockMvc.perform(
            get("/schedules/1/session")
                .contentType("application/json")
        )
            .andExpect(status().isNoContent)
    }

}